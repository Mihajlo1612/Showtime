package org.mihajlo1612.showtime.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.mihajlo1612.showtime.data.local.db.dao.FavoriteDao
import org.mihajlo1612.showtime.data.local.db.dao.GenreDao
import org.mihajlo1612.showtime.data.local.db.dao.MovieDao
import org.mihajlo1612.showtime.data.local.db.entity.FavoriteEntity
import org.mihajlo1612.showtime.data.local.db.entity.GenreEntity
import org.mihajlo1612.showtime.data.local.db.entity.MovieGenreCrossRef
import org.mihajlo1612.showtime.data.mapper.toDomain
import org.mihajlo1612.showtime.data.mapper.toEntity
import org.mihajlo1612.showtime.data.remote.api.FavoritesApi
import org.mihajlo1612.showtime.domain.model.Movie
import org.mihajlo1612.showtime.domain.repository.FavoritesRepository
import kotlin.time.Clock

class FavoritesRepositoryImpl(
    private val favoritesApi: FavoritesApi,
    private val movieDao: MovieDao,
    private val genreDao: GenreDao,
    private val favoriteDao: FavoriteDao,
) : FavoritesRepository {

    override fun observeFavorites(): Flow<List<Movie>> =
        movieDao.observeFavorites().map { list -> list.map { it.toDomain() } }

    override suspend fun syncFavorites() {
        val remote = favoritesApi.getFavorites()

        val genres = remote.flatMap { it.genres }.distinctBy { it.id }
        genreDao.upsertAll(genres.map { GenreEntity(id = it.id, names = it.name) })
        movieDao.upsertAll(remote.map { it.toEntity() })
        genreDao.upsertCrossRefs(
            remote.flatMap { m -> m.genres.map { MovieGenreCrossRef(movieId = m.imdbId, genreId = it.id) } }
        )

        favoriteDao.clearAll()
        val now = Clock.System.now().toEpochMilliseconds()
        remote.forEach { favoriteDao.insert(FavoriteEntity(movieId = it.imdbId, addedAt = now)) }
    }

    override suspend fun addFavorite(movieId: String): Result<Unit> {

        favoriteDao.insert(FavoriteEntity(movieId = movieId, addedAt = Clock.System.now().toEpochMilliseconds()))
        return try {
            favoritesApi.addFavorite(movieId)
            Result.success(Unit)
        } catch (e: Exception) {
            favoriteDao.deleteById(movieId)
            Result.failure(e)
        }
    }

    override suspend fun removeFavorite(movieId: String): Result<Unit> {
        favoriteDao.deleteById(movieId)
        return try {
            favoritesApi.removeFavorite(movieId)
            Result.success(Unit)
        } catch (e: Exception) {
            favoriteDao.insert(FavoriteEntity(movieId = movieId, addedAt = Clock.System.now().toEpochMilliseconds())) // rollback
            Result.failure(e)
        }
    }
}