package org.mihajlo1612.showtime.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import org.mihajlo1612.showtime.data.remote.model.RemoteMovieListItem

class WatchlistApi(private val client: HttpClient) {
    suspend fun getWatchlist(): List<RemoteMovieListItem> =
        client.get("me/watchlist").body()

    suspend fun addToWatchlist(imdbId: String) {
        client.post("me/watchlist/$imdbId")
    }

    suspend fun removeFromWatchlist(imdbId: String) {
        client.delete("me/watchlist/$imdbId")
    }
}