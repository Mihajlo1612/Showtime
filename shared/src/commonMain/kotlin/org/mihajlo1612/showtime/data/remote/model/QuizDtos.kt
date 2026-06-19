package org.mihajlo1612.showtime.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubmitQuizRequest(
    val score: Float,
    val category: Int,
)

@Serializable
data class PostQuizResultResponse(
    val result: QuizResultDto,
    val ranking: Int,
)

@Serializable
data class QuizResultDto(
    val id: Int,
    val category: Int,
    val score: Float,
    @SerialName("played_at") val playedAt: Long,
)