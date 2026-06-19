package org.mihajlo1612.showtime.domain.model

data class QuizSession(
    val id: Long = 0,
    val score: Float,
    val correctAnswers: Int,
    val totalQuestions: Int,
    val timeUsedSeconds: Int,
    val playedAt: Long,
)
