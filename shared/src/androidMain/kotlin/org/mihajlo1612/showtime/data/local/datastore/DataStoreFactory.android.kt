package org.mihajlo1612.showtime.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import okio.Path.Companion.toPath


fun createDataStore(context: Context): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = {

            context.filesDir.resolve("showtime_auth.preferences_pb").absolutePath.toPath(

            )
        }
    )

