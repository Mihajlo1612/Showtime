package org.mihajlo1612.showtime.data.local.db.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "movie_genre_cross_ref",
    primaryKeys = ["movieId", "genreId"],
    indices = [Index("genreId")],
)
data class MovieGenreCrossRef(
    val movieId: String,
    val genreId: Int,
)
