package org.mihajlo1612.showtime.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.mihajlo1612.showtime.data.local.db.entity.WatchlistEntity

@Dao
interface WatchlistDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: WatchlistEntity)

    @Query("DELETE FROM watchlist WHERE movieId = :movieId")
    suspend fun deleteById(movieId: String)

    @Query("DELETE FROM watchlist")
    suspend fun clearAll()

    @Query("SELECT movieId FROM watchlist")
    suspend fun getAllIds(): List<String>
}