package org.mihajlo1612.showtime.domain.model

data class Movie(
    val imdbId: String,
    val title: String,
    val year: Int?,
    val imdbRating: Float?,
    val imdbVotes: Int?,
    val posterPath: String?,
    val backdropPath: String?,
    val genres: List<Genre>,
    val isFavorite: Boolean,
    val inWatchlist: Boolean,
)