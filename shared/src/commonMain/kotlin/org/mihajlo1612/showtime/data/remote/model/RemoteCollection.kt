package org.mihajlo1612.showtime.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class RemoteCollection(
    val id: Int,
    val name: String,
    val posterPath: String?,
    val backdropPath: String?,
)
