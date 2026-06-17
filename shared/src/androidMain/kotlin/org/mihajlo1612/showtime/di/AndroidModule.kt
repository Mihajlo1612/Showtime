package org.mihajlo1612.showtime.di

import android.content.Context
import org.koin.dsl.module
import org.mihajlo1612.showtime.data.local.datastore.createDataStore

fun androidModule(context: Context) = module {
    single { createDataStore(context) }
}