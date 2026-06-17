package org.mihajlo1612.showtime.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.mihajlo1612.showtime.data.remote.model.AuthResponse
import org.mihajlo1612.showtime.data.remote.model.LoginRequest
import org.mihajlo1612.showtime.data.remote.model.RegisterRequest

class AuthApi(private val client: HttpClient) {

    suspend fun login(request: LoginRequest): AuthResponse =
        client.post("auth/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    suspend fun register(request: RegisterRequest): AuthResponse =
        client.post("auth/signup") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
}