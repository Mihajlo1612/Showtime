package org.mihajlo1612.showtime.ui.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mihajlo1612.showtime.domain.model.Genre
import org.mihajlo1612.showtime.domain.model.Movie
import org.mihajlo1612.showtime.domain.repository.MovieRepository

data class CatalogUiState(
    val allMovies: List<Movie> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 1,
    val hasMore: Boolean = true,
    val searchQuery: String = "",
    val selectedGenreId: Int? = null,
) {
    val genres: List<Genre> get() = allMovies.flatMap { it.genres }.distinctBy { it.id }.sortedBy { it.name }

    val displayedMovies: List<Movie>
        get() = allMovies
            .let { list ->
                if (searchQuery.isBlank()) list
                else list.filter { it.title.contains(searchQuery, ignoreCase = true) }
            }
            .let { list ->
                if (selectedGenreId == null) list
                else list.filter { movie -> movie.genres.any { it.id == selectedGenreId } }
            }
}

sealed class CatalogUiEvent {
    data class SearchChanged(val query: String) : CatalogUiEvent()
    data class GenreSelected(val genreId: Int?) : CatalogUiEvent()
    data object Refresh : CatalogUiEvent()
    data object LoadMore : CatalogUiEvent()
    data class MovieClicked(val imdbId: String) : CatalogUiEvent()
}

sealed class CatalogSideEffect {
    data class NavigateToDetail(val imdbId: String) : CatalogSideEffect()
}

class CatalogViewModel(private val movieRepository: MovieRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(CatalogUiState())
    val uiState: StateFlow<CatalogUiState> = _uiState.asStateFlow()

    private val _sideEffect = Channel<CatalogSideEffect>(Channel.BUFFERED)
    val sideEffect = _sideEffect.receiveAsFlow()

    init {
        viewModelScope.launch {
            movieRepository.observeMovies().collect { movies ->
                _uiState.update { it.copy(allMovies = movies) }
            }
        }
        syncPage(1)
    }

    fun onEvent(event: CatalogUiEvent) {
        when (event) {
            is CatalogUiEvent.SearchChanged ->
                _uiState.update { it.copy(searchQuery = event.query) }
            is CatalogUiEvent.GenreSelected ->
                _uiState.update { it.copy(selectedGenreId = event.genreId) }
            is CatalogUiEvent.Refresh -> {
                _uiState.update { it.copy(currentPage = 1, hasMore = true) }
                syncPage(1)
            }
            is CatalogUiEvent.LoadMore -> {
                val state = _uiState.value
                if (state.hasMore && !state.isLoading) syncPage(state.currentPage + 1)
            }
            is CatalogUiEvent.MovieClicked -> viewModelScope.launch {
                _sideEffect.send(CatalogSideEffect.NavigateToDetail(event.imdbId))
            }
        }
    }

    private fun syncPage(page: Int) {
        if (_uiState.value.isLoading) return
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                movieRepository.syncMovies(page)
                _uiState.update { it.copy(isLoading = false, currentPage = page) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
