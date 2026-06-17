package org.mihajlo1612.showtime.di

import org.koin.dsl.module
import org.mihajlo1612.showtime.data.local.datastore.createDataStore

val desktopModule = module {
    single { createDataStore() }
}