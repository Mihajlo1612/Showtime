package org.mihajlo1612.showtime.data.local.db.entity

import androidx.room.Entity

@Entity(
    tableName = "movie_genre_cross_ref",
    primaryKeys = ["movieId", "genreId"],
)
data class MovieGenreCrossRef(
    val movieId: String,
    val genreId: Int,
)
