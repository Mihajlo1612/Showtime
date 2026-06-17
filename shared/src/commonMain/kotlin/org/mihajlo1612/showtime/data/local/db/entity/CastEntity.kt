package org.mihajlo1612.showtime.data.local.db.entity

import androidx.room.Entity

@Entity(
    tableName = "cast_members",
    primaryKeys = ["movieImdbId", "personImdbId"],
)
data class CastEntity(
    val movieImdbId: String,
    val personImdbId: String,
    val name: String,
    val professions: String?,
    val department: String?,
    val profilePath: String?,
    val displayOrder: Int,
)
