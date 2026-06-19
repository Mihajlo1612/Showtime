package org.mihajlo1612.showtime.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import org.mihajlo1612.showtime.data.remote.model.RemoteMovieListItem

class FavoritesApi(private val client: HttpClient) {
    suspend fun getFavorites(): List<RemoteMovieListItem> =
        client.get("me/favorites").body()

    suspend fun addFavorite(imdbId: String) {
        client.post("me/favorites/$imdbId")
    }

    suspend fun removeFavorite(imdbId: String) {
        client.delete("me/favorites/$imdbId")
    }
}