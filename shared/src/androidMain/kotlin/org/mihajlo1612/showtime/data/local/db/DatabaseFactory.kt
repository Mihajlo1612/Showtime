package org.mihajlo1612.showtime.data.local.db

import android.content.Context
import androidx.room.Room

actual fun createShowtimeDatabase(ctx: Any?): ShowtimeDatabase =
    Room.databaseBuilder(ctx as Context, ShowtimeDatabase::class.java, "showtime.db").build()