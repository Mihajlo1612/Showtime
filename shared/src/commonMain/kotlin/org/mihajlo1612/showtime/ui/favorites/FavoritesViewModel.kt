package org.mihajlo1612.showtime.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mihajlo1612.showtime.domain.model.Movie
import org.mihajlo1612.showtime.domain.repository.FavoritesRepository

data class FavoritesUiState(
    val movies: List<Movie> = emptyList(),
    val isLoading: Boolean = true,
)

sealed class FavoritesUiEvent {
    data class MovieClicked(val imdbId: String) : FavoritesUiEvent()
    data class RemoveClicked(val imdbId: String) : FavoritesUiEvent()
}

sealed class FavoritesSideEffect {
    data class NavigateToDetail(val imdbId: String) : FavoritesSideEffect()
    data class ShowMessage(val text: String) : FavoritesSideEffect()
}

class FavoritesViewModel(
    private val favoritesRepository: FavoritesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    private val _sideEffect = Channel<FavoritesSideEffect>(Channel.BUFFERED)
    val sideEffect = _sideEffect.receiveAsFlow()

    init {
        viewModelScope.launch {
            favoritesRepository.observeFavorites().collect { movies ->
                _uiState.update { it.copy(movies = movies, isLoading = false) }
            }
        }
        viewModelScope.launch {
            try {
                favoritesRepository.syncFavorites()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onEvent(event: FavoritesUiEvent) {
        when (event) {
            is FavoritesUiEvent.MovieClicked -> viewModelScope.launch {
                _sideEffect.send(FavoritesSideEffect.NavigateToDetail(event.imdbId))
            }
            is FavoritesUiEvent.RemoveClicked -> viewModelScope.launch {
                favoritesRepository.removeFavorite(event.imdbId).onFailure {
                    _sideEffect.send(FavoritesSideEffect.ShowMessage("Couldn't remove — restored."))
                }
            }
        }
    }
}