package org.mihajlo1612.showtime.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class RemoteMovieListItem(
    val imdbId: String,
    val title: String,
    val year: Int?,
    val imdbRating: Float?,
    val imdbVotes: Int?,
    val posterPath: String?,
    val genres: List<RemoteGenre> = emptyList(),
)