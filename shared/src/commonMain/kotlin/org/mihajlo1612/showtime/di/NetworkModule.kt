package org.mihajlo1612.showtime.di

import io.ktor.client.HttpClient
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.url
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import org.mihajlo1612.showtime.data.local.datastore.AuthDataStore
import org.mihajlo1612.showtime.data.remote.api.AuthApi

val networkModule = module {

    single {
        val authDataStore = get<AuthDataStore>()
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
            install(Logging) {
                level = LogLevel.BODY
            }
            install(createClientPlugin("AuthHeader") {
                onRequest { request, _ ->
                    val token = authDataStore.getToken()
                    if (token != null) {
                        request.headers.append(HttpHeaders.Authorization, "Bearer $token")
                    }
                }
                onResponse { response ->
                    if (response.status == io.ktor.http.HttpStatusCode.Unauthorized) {
                        authDataStore.clearToken()   // observeLoginState=false → App.kt vraća na landing
                    }
                }
            })
            defaultRequest {
                url("https://rma.finlab.rs/")
            }
        }
    }

    single { AuthApi(get()) }
}