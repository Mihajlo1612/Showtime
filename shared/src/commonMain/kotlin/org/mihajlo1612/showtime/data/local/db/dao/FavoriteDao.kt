package org.mihajlo1612.showtime.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.mihajlo1612.showtime.data.local.db.entity.FavoriteEntity

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites")
    suspend fun clearAll()

    @Query("SELECT movieId FROM favorites")
    suspend fun getAllIds(): List<String>

    @Query("DELETE FROM favorites WHERE movieId = :movieId")
    suspend fun deleteById(movieId: String)

    @Query("SELECT movieId FROM favorites")
    fun observeIds(): kotlinx.coroutines.flow.Flow<List<String>>
}