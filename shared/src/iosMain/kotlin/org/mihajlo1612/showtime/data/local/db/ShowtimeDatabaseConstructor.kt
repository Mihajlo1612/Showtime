package org.mihajlo1612.showtime.data.local.db

import androidx.room.RoomDatabaseConstructor

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object ShowtimeDatabaseConstructor : RoomDatabaseConstructor<ShowtimeDatabase> {
    actual override fun initialize(): ShowtimeDatabase =
        error("iOS database initialization handled by Room KSP on macOS")
}