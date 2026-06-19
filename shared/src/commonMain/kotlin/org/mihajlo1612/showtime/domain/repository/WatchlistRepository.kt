package org.mihajlo1612.showtime.domain.repository

import kotlinx.coroutines.flow.Flow
import org.mihajlo1612.showtime.domain.model.Movie

interface WatchlistRepository {
    fun observeWatchlist(): Flow<List<Movie>>
    suspend fun syncWatchlist()
    suspend fun addToWatchlist(movieId: String): Result<Unit>
    suspend fun removeFromWatchlist(movieId: String): Result<Unit>
}