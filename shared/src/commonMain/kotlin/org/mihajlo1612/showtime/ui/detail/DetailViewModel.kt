package org.mihajlo1612.showtime.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mihajlo1612.showtime.domain.model.CastMember
import org.mihajlo1612.showtime.domain.model.MovieDetail
import org.mihajlo1612.showtime.domain.repository.FavoritesRepository
import org.mihajlo1612.showtime.domain.repository.MovieRepository
import org.mihajlo1612.showtime.domain.repository.WatchlistRepository

data class DetailUiState(
    val movie: MovieDetail? = null,
    val cast: List<CastMember> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isFavorite: Boolean = false,
    val isInWatchlist: Boolean = false,
)

sealed class DetailUiEvent {
    data object ToggleFavorite : DetailUiEvent()
    data object ToggleWatchlist : DetailUiEvent()
}

sealed class DetailSideEffect {
    data class ShowMessage(val text: String) : DetailSideEffect()
}

class DetailViewModel(
    private val movieRepository: MovieRepository,
    private val favoritesRepository: FavoritesRepository,
    private val watchlistRepository: WatchlistRepository,
    private val imdbId: String,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private val _sideEffect = Channel<DetailSideEffect>(Channel.BUFFERED)
    val sideEffect = _sideEffect.receiveAsFlow()

    init {
        viewModelScope.launch {
            movieRepository.observeMovieDetail(imdbId).collect { movie ->
                _uiState.update { it.copy(movie = movie) }
            }
        }
        viewModelScope.launch {
            movieRepository.observeCast(imdbId).collect { cast ->
                _uiState.update { it.copy(cast = cast) }
            }
        }
        viewModelScope.launch {
            favoritesRepository.observeFavorites().collect { favs ->
                _uiState.update { it.copy(isFavorite = favs.any { m -> m.imdbId == imdbId }) }
            }
        }
        viewModelScope.launch {
            watchlistRepository.observeWatchlist().collect { wl ->
                _uiState.update { it.copy(isInWatchlist = wl.any { m -> m.imdbId == imdbId }) }
            }
        }
        syncDetail()
    }

    fun onEvent(event: DetailUiEvent) {
        when (event) {
            is DetailUiEvent.ToggleFavorite -> toggleFavorite()
            is DetailUiEvent.ToggleWatchlist -> toggleWatchlist()
        }
    }

    private fun toggleFavorite() {
        viewModelScope.launch {
            val wasFavorite = _uiState.value.isFavorite
            val result = if (wasFavorite) favoritesRepository.removeFavorite(imdbId)
            else favoritesRepository.addFavorite(imdbId)
            result.onFailure {
                _sideEffect.send(
                    DetailSideEffect.ShowMessage(
                        if (wasFavorite) "Couldn't remove — restored." else "Couldn't add — restored."
                    )
                )
            }
        }
    }

    private fun toggleWatchlist() {
        viewModelScope.launch {
            val wasIn = _uiState.value.isInWatchlist
            val result = if (wasIn) watchlistRepository.removeFromWatchlist(imdbId)
            else watchlistRepository.addToWatchlist(imdbId)
            result.onFailure {
                _sideEffect.send(DetailSideEffect.ShowMessage(
                    if (wasIn) "Couldn't remove — restored." else "Couldn't add — restored."
                ))
            }
        }
    }

    private fun syncDetail() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                movieRepository.syncMovieDetail(imdbId)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
            try {
                movieRepository.syncCast(imdbId)
            } catch (e: Exception) {
                println("CAST SYNC FAILED for $imdbId: ${e::class.simpleName}: ${e.message}")
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}