package org.mihajlo1612.showtime.domain.repository

import kotlinx.coroutines.flow.Flow
import org.mihajlo1612.showtime.domain.model.CastMember
import org.mihajlo1612.showtime.domain.model.Movie
import org.mihajlo1612.showtime.domain.model.MovieDetail

interface MovieRepository {
    fun observeMovies(): Flow<List<Movie>>
    fun observeMovieDetail(imdbId: String): Flow<MovieDetail?>
    fun observeCast(imdbId: String): Flow<List<CastMember>>
    suspend fun syncMovies(page: Int = 1, pageSize: Int = 20)
    suspend fun syncMovieDetail(imdbId: String)
    suspend fun syncCast(imdbId: String)
    suspend fun getLeadActor(imdbId: String): CastMember?
}