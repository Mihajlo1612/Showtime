package org.mihajlo1612.showtime

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.mihajlo1612.showtime.di.androidModule
import org.mihajlo1612.showtime.di.appModule

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApplication)
            modules(androidModule, appModule)
        }
    }
}