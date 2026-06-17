package org.mihajlo1612.showtime.data.local.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import org.mihajlo1612.showtime.data.local.db.entity.MovieEntity
import org.mihajlo1612.showtime.data.local.db.relation.MovieWithGenres

@Dao
interface MovieDao {

    @Upsert
    suspend fun upsertAll(movies: List<MovieEntity>)

    @Upsert
    suspend fun upsertMovie(movie: MovieEntity)

    @Transaction
    @Query("SELECT * FROM movies ORDER BY title ASC")
    fun observeAll(): Flow<List<MovieWithGenres>>

    @Transaction
    @Query("SELECT * FROM movies WHERE imdbId = :imdbId")
    fun observeById(imdbId: String): Flow<MovieWithGenres?>

    @Transaction
    @Query("""
        SELECT movies.* FROM movies
        INNER JOIN favorites ON movies.imdbId = favorites.movieId
        ORDER BY favorites.addedAt DESC
    """)
    fun observeFavorites(): Flow<List<MovieWithGenres>>

    @Transaction
    @Query("""
        SELECT movies.* FROM movies
        INNER JOIN watchlist ON movies.imdbId = watchlist.movieId
        ORDER BY watchlist.addedAt DESC
    """)
    fun observeWatchlist(): Flow<List<MovieWithGenres>>

    @Query("SELECT * FROM movies WHERE imdbId IN (:ids)")
    suspend fun getByIds(ids: List<String>): List<MovieEntity>
}