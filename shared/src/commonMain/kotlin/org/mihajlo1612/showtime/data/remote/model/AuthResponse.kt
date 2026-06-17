package org.mihajlo1612.showtime.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("expires_in") val expiresIn: Long,
    val user: RemoteUser,
)

@Serializable
data class RemoteUser(
    val id: Int,
    val username: String,
    @SerialName("full_name") val fullName: String,
)
