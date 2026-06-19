package org.mihajlo1612.showtime.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mihajlo1612.showtime.domain.repository.AuthRepository
import org.mihajlo1612.showtime.domain.repository.FavoritesRepository
import org.mihajlo1612.showtime.domain.repository.QuizRepository
import org.mihajlo1612.showtime.domain.repository.WatchlistRepository

data class ProfileUiState(
    val fullName: String = "",
    val username: String = "",
    val bestScore: Float = 0f,
    val gamesPlayed: Int = 0,
    val favoritesCount: Int = 0,
    val watchlistCount: Int = 0,
    val isLoading: Boolean = true,
)

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val favoritesRepository: FavoritesRepository,
    private val watchlistRepository: WatchlistRepository,
    private val quizRepository: QuizRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val user = authRepository.getMe()
                _uiState.update {
                    it.copy(fullName = user.fullName, username = user.username, isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
        viewModelScope.launch {
            quizRepository.observeSessions().collect { sessions ->
                _uiState.update {
                    it.copy(
                        bestScore = sessions.maxOfOrNull { s -> s.score } ?: 0f,
                        gamesPlayed = sessions.size,
                    )
                }
            }
        }
        viewModelScope.launch {
            favoritesRepository.observeFavorites().collect { list ->
                _uiState.update { it.copy(favoritesCount = list.size) }
            }
        }
        viewModelScope.launch {
            watchlistRepository.observeWatchlist().collect { list ->
                _uiState.update { it.copy(watchlistCount = list.size) }
            }
        }
    }
}