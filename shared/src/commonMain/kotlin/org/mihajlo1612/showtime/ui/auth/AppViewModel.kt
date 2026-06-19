package org.mihajlo1612.showtime.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.mihajlo1612.showtime.data.local.db.dao.FavoriteDao
import org.mihajlo1612.showtime.data.local.db.dao.WatchlistDao
import org.mihajlo1612.showtime.domain.repository.AuthRepository

class AppViewModel(
    private val authRepository: AuthRepository,
    private val favoriteDao: FavoriteDao,
    private val watchlistDao: WatchlistDao,
) : ViewModel() {

    val isLoggedIn: StateFlow<Boolean?> = authRepository
        .observeLoginState()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun logout() {
        viewModelScope.launch {
            favoriteDao.clearAll()
            watchlistDao.clearAll()
            authRepository.logout()   // token poslednji → observeLoginState=false → landing
        }
    }
}