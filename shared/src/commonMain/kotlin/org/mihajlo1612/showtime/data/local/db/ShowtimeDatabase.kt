package org.mihajlo1612.showtime.data.local.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import org.mihajlo1612.showtime.data.local.db.dao.CastDao
import org.mihajlo1612.showtime.data.local.db.dao.FavoriteDao
import org.mihajlo1612.showtime.data.local.db.dao.GenreDao
import org.mihajlo1612.showtime.data.local.db.dao.MovieDao
import org.mihajlo1612.showtime.data.local.db.dao.QuizSessionDao
import org.mihajlo1612.showtime.data.local.db.dao.WatchlistDao
import org.mihajlo1612.showtime.data.local.db.entity.CastEntity
import org.mihajlo1612.showtime.data.local.db.entity.FavoriteEntity
import org.mihajlo1612.showtime.data.local.db.entity.GenreEntity
import org.mihajlo1612.showtime.data.local.db.entity.MovieEntity
import org.mihajlo1612.showtime.data.local.db.entity.MovieGenreCrossRef
import org.mihajlo1612.showtime.data.local.db.entity.QuizSessionEntity
import org.mihajlo1612.showtime.data.local.db.entity.WatchlistEntity

@ConstructedBy(ShowtimeDatabaseConstructor::class)
@Database(
    entities = [
        MovieEntity::class,
        GenreEntity::class,
        MovieGenreCrossRef::class,
        CastEntity::class,
        FavoriteEntity::class,
        WatchlistEntity::class,
        QuizSessionEntity::class
    ],
    version = 1,
)
abstract class ShowtimeDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun genreDao(): GenreDao
    abstract fun castDao(): CastDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun watchlistDao(): WatchlistDao
    abstract fun quizSessionDao(): QuizSessionDao
}