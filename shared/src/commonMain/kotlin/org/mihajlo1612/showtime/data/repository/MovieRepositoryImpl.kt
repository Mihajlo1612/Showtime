package org.mihajlo1612.showtime.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.mihajlo1612.showtime.data.local.db.dao.CastDao
import org.mihajlo1612.showtime.data.local.db.dao.GenreDao
import org.mihajlo1612.showtime.data.local.db.dao.MovieDao
import org.mihajlo1612.showtime.data.local.db.entity.GenreEntity
import org.mihajlo1612.showtime.data.local.db.entity.MovieGenreCrossRef
import org.mihajlo1612.showtime.data.remote.api.MovieApi
import org.mihajlo1612.showtime.data.mapper.toDetailDomain
import org.mihajlo1612.showtime.data.mapper.toDomain
import org.mihajlo1612.showtime.data.mapper.toEntity
import org.mihajlo1612.showtime.data.mapper.toGenreEntities
import org.mihajlo1612.showtime.domain.model.CastMember
import org.mihajlo1612.showtime.domain.model.Movie
import org.mihajlo1612.showtime.domain.model.MovieDetail
import org.mihajlo1612.showtime.domain.repository.MovieRepository

class MovieRepositoryImpl(
    private val movieApi: MovieApi,
    private val movieDao: MovieDao,
    private val genreDao: GenreDao,
    private val castDao: CastDao,
) : MovieRepository {

    override fun observeMovies(): Flow<List<Movie>> =
        movieDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeMovieDetail(imdbId: String): Flow<MovieDetail?> =
        movieDao.observeById(imdbId).map { it?.toDetailDomain() }

    override fun observeCast(imdbId: String): Flow<List<CastMember>> =
        castDao.observeByMovie(imdbId).map { list -> list.map { it.toDomain() } }

    override suspend fun syncMovies(page: Int, pageSize: Int) {
        val response = movieApi.getMovies(page)
        val items = response.items

        val genres = items.flatMap { it.genres }.distinctBy { it.id }
        genreDao.upsertAll(genres.map { GenreEntity(id = it.id, names = it.name) })
        movieDao.upsertAll(items.map { it.toEntity() })

        val crossRef = items.flatMap { movie ->
            movie.genres.map { genre ->
                MovieGenreCrossRef(movieId = movie.imdbId, genreId = genre.id)
            }
        }
        genreDao.upsertCrossRefs(crossRef)
    }

    override suspend fun syncMovieDetail(imdbId: String) {
        val detail = movieApi.getMovieDetail(imdbId)
        movieDao.upsertMovie(detail.toEntity())
        genreDao.upsertAll(detail.toGenreEntities())
        genreDao.upsertCrossRefs(
            detail.genres.map { MovieGenreCrossRef(movieId = imdbId, genreId = it.id) }
        )
    }

    override suspend fun syncCast(imdbId: String) {
        val response = movieApi.getCast(imdbId, page = 1, pageSize = 20)
        val entities = response.items.mapIndexed { index, person ->
            person.toEntity(movieImdbId = imdbId, displayOrder = index)
        }
        castDao.upsertAll(entities)
    }

    override suspend fun getLeadActor(imdbId: String): CastMember? =
        castDao.getLeadActor(imdbId)?.toDomain()
}