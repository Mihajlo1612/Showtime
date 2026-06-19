package org.mihajlo1612.showtime.data.local.db

import androidx.room.Room

actual fun createShowtimeDatabase(ctx: Any?): ShowtimeDatabase =
    Room.databaseBuilder<ShowtimeDatabase>("showtime.db").build()