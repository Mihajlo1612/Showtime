package org.mihajlo1612.showtime.data.local.datastore

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import okio.Path.Companion.toPath
import platform.Foundation.NSHomeDirectory

fun createDataStore(): DataStore<Preferences> =

    PreferenceDataStoreFactory.createWithPath(
        produceFile = {
            "${NSHomeDirectory()}/showtime_auth.preferences_pb".toPath() }
    )