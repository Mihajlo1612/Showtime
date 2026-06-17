package org.mihajlo1612.showtime.data.local.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import org.mihajlo1612.showtime.data.local.db.entity.CastEntity

@Dao
interface CastDao {

    @Upsert
    suspend fun upsertAll(cast: List<CastEntity>)

    @Query("SELECT * FROM cast_members WHERE movieImdbId = :movieImdbId ORDER BY displayOrder ASC")
    fun observeByMovie(movieImdbId: String): Flow<List<CastEntity>>

    @Query("SELECT * FROM cast_members WHERE movieImdbId = :movieImdbId ORDER BY displayOrder ASC LIMIT 1")
    suspend fun getLeadActor(movieImdbId: String): CastEntity?
}