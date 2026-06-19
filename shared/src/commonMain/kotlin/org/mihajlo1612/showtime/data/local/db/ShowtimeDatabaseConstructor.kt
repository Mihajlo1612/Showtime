package org.mihajlo1612.showtime.data.local.db

import androidx.room.RoomDatabaseConstructor

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object ShowtimeDatabaseConstructor : RoomDatabaseConstructor<ShowtimeDatabase> {
    override fun initialize(): ShowtimeDatabase
}