package org.mihajlo1612.showtime.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import org.mihajlo1612.showtime.data.remote.model.PaginatedResponse
import org.mihajlo1612.showtime.data.remote.model.RemoteGenre
import org.mihajlo1612.showtime.data.remote.model.RemoteMovieDetail
import org.mihajlo1612.showtime.data.remote.model.RemoteMovieListItem
import org.mihajlo1612.showtime.data.remote.model.RemotePersonSummary

class MovieApi(private val client: HttpClient) {

    suspend fun getMovies(page: Int = 1): PaginatedResponse<RemoteMovieListItem> = client.get("movies") {
        parameter("page", page)
    }.body()

    suspend fun getMovieDetail(imdbId: String): RemoteMovieDetail =
        client.get("movies/$imdbId").body()

    suspend fun getGenres(): List<RemoteGenre> =
        client.get("genre").body()

    suspend fun getCast(
        imdbId: String,
        page: Int = 1,
        pageSize: Int = 20,
    ): PaginatedResponse<RemotePersonSummary> =
        client.get("movies/$imdbId/cast") {
            parameter("page", page)
            parameter("page_size", pageSize)
        }.body()
}