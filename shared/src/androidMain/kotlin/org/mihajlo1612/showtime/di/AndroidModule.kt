package org.mihajlo1612.showtime.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.mihajlo1612.showtime.data.local.datastore.createDataStore
import org.mihajlo1612.showtime.data.local.db.ShowtimeDatabase
import org.mihajlo1612.showtime.data.local.db.createShowtimeDatabase
import org.mihajlo1612.showtime.data.local.db.dao.CastDao
import org.mihajlo1612.showtime.data.local.db.dao.FavoriteDao
import org.mihajlo1612.showtime.data.local.db.dao.GenreDao
import org.mihajlo1612.showtime.data.local.db.dao.MovieDao
import org.mihajlo1612.showtime.data.local.db.dao.QuizSessionDao
import org.mihajlo1612.showtime.data.local.db.dao.WatchlistDao

val androidModule = module {
    single { createDataStore(androidContext()) }
    single { createShowtimeDatabase(androidContext()) }
    single<MovieDao> { get<ShowtimeDatabase>().movieDao() }
    single<GenreDao> { get<ShowtimeDatabase>().genreDao() }
    single<CastDao> { get<ShowtimeDatabase>().castDao() }
    single<QuizSessionDao> { get<ShowtimeDatabase>().quizSessionDao() }
    single<FavoriteDao> { get<ShowtimeDatabase>().favoriteDao() }
    single<WatchlistDao> { get<ShowtimeDatabase>().watchlistDao() }
}