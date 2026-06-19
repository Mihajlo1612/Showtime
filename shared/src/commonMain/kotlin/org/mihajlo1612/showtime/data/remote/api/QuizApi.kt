package org.mihajlo1612.showtime.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import org.mihajlo1612.showtime.data.remote.model.PostQuizResultResponse
import org.mihajlo1612.showtime.data.remote.model.SubmitQuizRequest

class QuizApi(private val client: HttpClient) {
    suspend fun submitResult(score: Float, category: Int = 1): PostQuizResultResponse {
        val response: HttpResponse = client.post("leaderboard") {
            contentType(ContentType.Application.Json)
            setBody(SubmitQuizRequest(score = score, category = category))
        }
        if (!response.status.isSuccess()) {
            val body = response.bodyAsText().take(180)
            throw IllegalStateException("HTTP ${response.status.value}: $body")
        }
        return response.body()
    }
}