package org.mihajlo1612.showtime.ui.watchlist

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
import org.mihajlo1612.showtime.domain.repository.WatchlistRepository

data class WatchlistUiState(
    val movies: List<Movie> = emptyList(),
    val isLoading: Boolean = true,
)

sealed class WatchlistUiEvent {
    data class MovieClicked(val imdbId: String) : WatchlistUiEvent()
    data class RemoveClicked(val imdbId: String) : WatchlistUiEvent()
}

sealed class WatchlistSideEffect {
    data class NavigateToDetail(val imdbId: String) : WatchlistSideEffect()
    data class ShowMessage(val text: String) : WatchlistSideEffect()
}

class WatchlistViewModel(
    private val watchlistRepository: WatchlistRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(WatchlistUiState())
    val uiState: StateFlow<WatchlistUiState> = _uiState.asStateFlow()

    private val _sideEffect = Channel<WatchlistSideEffect>(Channel.BUFFERED)
    val sideEffect = _sideEffect.receiveAsFlow()

    init {
        viewModelScope.launch {
            watchlistRepository.observeWatchlist().collect { movies ->
                _uiState.update { it.copy(movies = movies, isLoading = false) }
            }
        }
        viewModelScope.launch {
            try {
                watchlistRepository.syncWatchlist()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onEvent(event: WatchlistUiEvent) {
        when (event) {
            is WatchlistUiEvent.MovieClicked -> viewModelScope.launch {
                _sideEffect.send(WatchlistSideEffect.NavigateToDetail(event.imdbId))
            }
            is WatchlistUiEvent.RemoveClicked -> viewModelScope.launch {
                watchlistRepository.removeFromWatchlist(event.imdbId).onFailure {
                    _sideEffect.send(WatchlistSideEffect.ShowMessage("Couldn't remove — restored."))
                }
            }
        }
    }
}