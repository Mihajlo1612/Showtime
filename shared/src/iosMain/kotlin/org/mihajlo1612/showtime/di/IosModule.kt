package org.mihajlo1612.showtime.di

import org.koin.dsl.module
import org.mihajlo1612.showtime.data.local.datastore.createDataStore

fun iosModule() = module {
    single { createDataStore() }
}

fun initKoinIos() = initKoin(iosModule())