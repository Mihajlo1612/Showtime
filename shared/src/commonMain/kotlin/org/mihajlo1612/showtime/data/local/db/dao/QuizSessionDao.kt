package org.mihajlo1612.showtime.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.mihajlo1612.showtime.data.local.db.entity.QuizSessionEntity

@Dao
interface QuizSessionDao {

    @Insert
    suspend fun insert(session: QuizSessionEntity)

    @Query("SELECT * FROM quiz_sessions ORDER BY playedAt DESC")
    fun observeAll(): Flow<List<QuizSessionEntity>>

    @Query("SELECT * FROM quiz_sessions ORDER BY playedAt DESC LIMIT 1")
    suspend fun getLatest(): QuizSessionEntity?
}