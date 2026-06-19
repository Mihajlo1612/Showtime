package org.mihajlo1612.showtime.data.local.db

import androidx.room.Room
import org.mihajlo1612.showtime.data.local.db.ShowtimeDatabase
import platform.Foundation.NSHomeDirectory

actual fun createShowtimeDatabase(ctx: Any?): ShowtimeDatabase =
    Room.databaseBuilder<ShowtimeDatabase>(
        name = NSHomeDirectory() + "/showtime.db"
    ).build()