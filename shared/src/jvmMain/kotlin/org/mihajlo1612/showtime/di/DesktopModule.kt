package org.mihajlo1612.showtime.di

import org.koin.dsl.module
import org.mihajlo1612.showtime.data.local.datastore.createDataStore
import org.mihajlo1612.showtime.data.local.db.ShowtimeDatabase
import org.mihajlo1612.showtime.data.local.db.createShowtimeDatabase

val desktopModule = module {
    single { createDataStore() }
    single { createShowtimeDatabase(null) }
    single { get<ShowtimeDatabase>().movieDao() }
    single { get<ShowtimeDatabase>().genreDao() }
}