package org.mihajlo1612.showtime.data.local.db.dao

import androidx.room.Dao
import androidx.room.Upsert
import org.mihajlo1612.showtime.data.local.db.entity.GenreEntity
import org.mihajlo1612.showtime.data.local.db.entity.MovieGenreCrossRef

@Dao
interface GenreDao {

    @Upsert
    suspend fun upsertAll(genres: List<GenreEntity>)

    @Upsert
    suspend fun upsertCrossRefs(crossRefs: List<MovieGenreCrossRef>)
}