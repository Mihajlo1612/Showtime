package org.mihajlo1612.showtime.data.mapper

import org.mihajlo1612.showtime.data.local.db.entity.QuizSessionEntity
import org.mihajlo1612.showtime.domain.model.QuizSession

fun QuizSession.toEntity() = QuizSessionEntity(
    id = id,
    score = score,
    correctAnswers = correctAnswers,
    totalQuestions = totalQuestions,
    timeUsedSeconds = timeUsedSeconds,
    playedAt = playedAt,
)

fun QuizSessionEntity.toDomain() = QuizSession(
    id = id,
    score = score,
    correctAnswers = correctAnswers,
    totalQuestions = totalQuestions,
    timeUsedSeconds = timeUsedSeconds,
    playedAt = playedAt,
)