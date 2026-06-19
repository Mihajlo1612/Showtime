package org.mihajlo1612.showtime.domain.repository

import kotlinx.coroutines.flow.Flow
import org.mihajlo1612.showtime.domain.model.QuizQuestion
import org.mihajlo1612.showtime.domain.model.QuizSession

interface QuizRepository {
    suspend fun ensureQuizPool()              // bootstrap ~100 filmova ako fali
    suspend fun hasEnoughForQuiz(): Boolean   // >= 10 filmova sa slikom
    suspend fun generateQuiz(): List<QuizQuestion>
    suspend fun submitResult(score: Float): Int   // vraca ranking; -1 ako padne
    suspend fun saveSession(session: QuizSession)
    fun observeSessions(): Flow<List<QuizSession>>
}