package org.mihajlo1612.showtime

import android.app.Application
import org.mihajlo1612.showtime.di.androidModule
import org.mihajlo1612.showtime.di.initKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(androidModule(this))
    }
}