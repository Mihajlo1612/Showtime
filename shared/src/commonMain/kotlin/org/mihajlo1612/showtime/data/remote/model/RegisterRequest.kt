package org.mihajlo1612.showtime.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val name: String,
    val username: String,
    val password: String,
)
