package org.mihajlo1612.showtime.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import okio.Path.Companion.toPath
import androidx.datastore.preferences.core.Preferences

fun createDataStore(): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = {
            "${System.getProperty("user.home")}/showtime_auth.preferences_pb".toPath() }
    )