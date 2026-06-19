package org.mihajlo1612.showtime.data.mapper


import org.mihajlo1612.showtime.data.local.db.entity.CastEntity
import org.mihajlo1612.showtime.data.local.db.entity.GenreEntity
import org.mihajlo1612.showtime.data.local.db.entity.MovieEntity
import org.mihajlo1612.showtime.data.local.db.relation.MovieWithGenres
import org.mihajlo1612.showtime.data.remote.model.RemoteMovieDetail
import org.mihajlo1612.showtime.data.remote.model.RemoteMovieListItem
import org.mihajlo1612.showtime.data.remote.model.RemotePersonSummary
import org.mihajlo1612.showtime.domain.model.CastMember
import org.mihajlo1612.showtime.domain.model.Genre
import org.mihajlo1612.showtime.domain.model.Movie
import org.mihajlo1612.showtime.domain.model.MovieDetail

fun RemoteMovieListItem.toEntity() = MovieEntity(
    imdbId = imdbId,
    tmdbId = null,
    title = title,
    originalTitle = null,
    overview = null,
    tagline = null,
    releaseDate = null,
    year = year,
    runtime = null,
    budget = null,
    revenue = null,
    languageCode = null,
    popularity = null,
    imdbRating = imdbRating,
    imdbVotes = imdbVotes,
    tmdbRating = null,
    tmdbVotes = null,
    posterPath = posterPath,
    backdropPath = null,
    homepage = null,
)

fun RemoteMovieDetail.toEntity() = MovieEntity(
    imdbId = imdbId,
    tmdbId = tmdbId,
    title = title,
    originalTitle = originalTitle,
    overview = overview,
    tagline = tagline,
    releaseDate = releaseDate,
    year = year,
    runtime = runtime,
    budget = budget,
    revenue = revenue,
    languageCode = languageCode,
    popularity = popularity,
    imdbRating = imdbRating,
    imdbVotes = imdbVotes,
    tmdbRating = tmdbRating,
    tmdbVotes = tmdbVotes,
    posterPath = posterPath,
    backdropPath = backdropPath,
    homepage = homepage,
)

fun RemoteMovieDetail.toGenreEntities() =
    genres.map { GenreEntity(id = it.id, names = it.name) }

fun MovieWithGenres.toDomain() = Movie(
    imdbId = movie.imdbId,
    title = movie.title,
    year = movie.year,
    imdbRating = movie.imdbRating,
    imdbVotes = movie.imdbVotes,
    posterPath = movie.posterPath,
    backdropPath = movie.backdropPath,
    genres = genres.map { Genre(id = it.id, name = it.names) },
    isFavorite = movie.isFavorite,
    inWatchlist = movie.inWatchlist,
)

fun MovieWithGenres.toDetailDomain() = MovieDetail(
    imdbId = movie.imdbId,
    tmdbId = movie.tmdbId,
    title = movie.title,
    originalTitle = movie.originalTitle,
    overview = movie.overview,
    tagline = movie.tagline,
    releaseDate = movie.releaseDate,
    year = movie.year,
    runtime = movie.runtime,
    budget = movie.budget,
    revenue = movie.revenue,
    languageCode = movie.languageCode,
    popularity = movie.popularity,
    imdbRating = movie.imdbRating,
    imdbVotes = movie.imdbVotes,
    tmdbRating = movie.tmdbRating,
    tmdbVotes = movie.tmdbVotes,
    posterPath = movie.posterPath,
    backdropPath = movie.backdropPath,
    homepage = movie.homepage,
    genres = genres.map { Genre(id = it.id, name = it.names) },
    isFavorite = movie.isFavorite,
    inWatchList = movie.inWatchlist,
)

fun CastEntity.toDomain() = CastMember(
    imdbId = personImdbId,
    name = name,
    professions = professions,
    department = department,
    profilePath = profilePath,
)

fun RemotePersonSummary.toEntity(movieImdbId: String, displayOrder: Int) = CastEntity(
    movieImdbId = movieImdbId,
    personImdbId = imdbId,
    name = name,
    professions = professions,
    department = department,
    profilePath = profilePath,
    displayOrder = displayOrder,
)