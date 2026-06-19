package org.mihajlo1612.showtime.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.mihajlo1612.showtime.data.local.db.dao.GenreDao
import org.mihajlo1612.showtime.data.local.db.dao.MovieDao
import org.mihajlo1612.showtime.data.local.db.dao.WatchlistDao
import org.mihajlo1612.showtime.data.local.db.entity.GenreEntity
import org.mihajlo1612.showtime.data.local.db.entity.MovieGenreCrossRef
import org.mihajlo1612.showtime.data.local.db.entity.WatchlistEntity
import org.mihajlo1612.showtime.data.mapper.toDomain
import org.mihajlo1612.showtime.data.mapper.toEntity
import org.mihajlo1612.showtime.data.remote.api.WatchlistApi
import org.mihajlo1612.showtime.domain.model.Movie
import org.mihajlo1612.showtime.domain.repository.WatchlistRepository
import kotlin.time.Clock

class WatchlistRepositoryImpl(
    private val watchlistApi: WatchlistApi,
    private val movieDao: MovieDao,
    private val genreDao: GenreDao,
    private val watchlistDao: WatchlistDao,
) : WatchlistRepository {

    override fun observeWatchlist(): Flow<List<Movie>> =
        movieDao.observeWatchlist().map { list -> list.map { it.toDomain() } }

    override suspend fun syncWatchlist() {
        val remote = watchlistApi.getWatchlist()
        val genres = remote.flatMap { it.genres }.distinctBy { it.id }
        genreDao.upsertAll(genres.map { GenreEntity(id = it.id, names = it.name) })
        movieDao.upsertAll(remote.map { it.toEntity() })
        genreDao.upsertCrossRefs(
            remote.flatMap { m -> m.genres.map { MovieGenreCrossRef(movieId = m.imdbId, genreId = it.id) } }
        )
        watchlistDao.clearAll()
        val now = Clock.System.now().toEpochMilliseconds()
        remote.forEach { watchlistDao.insert(WatchlistEntity(movieId = it.imdbId, addedAt = now)) }
    }

    override suspend fun addToWatchlist(movieId: String): Result<Unit> {
        watchlistDao.insert(WatchlistEntity(movieId = movieId, addedAt = Clock.System.now().toEpochMilliseconds()))
        return try {
            watchlistApi.addToWatchlist(movieId)
            Result.success(Unit)
        } catch (e: Exception) {
            watchlistDao.deleteById(movieId)
            Result.failure(e)
        }
    }

    override suspend fun removeFromWatchlist(movieId: String): Result<Unit> {
        watchlistDao.deleteById(movieId)
        return try {
            watchlistApi.removeFromWatchlist(movieId)
            Result.success(Unit)
        } catch (e: Exception) {
            watchlistDao.insert(WatchlistEntity(movieId = movieId, addedAt = Clock.System.now().toEpochMilliseconds()))
            Result.failure(e)
        }
    }
}