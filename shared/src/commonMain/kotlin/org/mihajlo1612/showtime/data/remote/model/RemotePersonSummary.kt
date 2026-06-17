package org.mihajlo1612.showtime.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class RemotePersonSummary(
    val imdbId: String,
    val name: String,
    val professions: String?,
    val department: String?,
    val profilePath: String?,
)
