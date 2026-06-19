package org.mihajlo1612.showtime.domain.repository

import kotlinx.coroutines.flow.Flow
import org.mihajlo1612.showtime.domain.model.Movie

interface FavoritesRepository {
    fun observeFavorites(): Flow<List<Movie>>
    suspend fun syncFavorites()
    suspend fun addFavorite(movieId: String): Result<Unit>
    suspend fun removeFavorite(movieId: String): Result<Unit>
}