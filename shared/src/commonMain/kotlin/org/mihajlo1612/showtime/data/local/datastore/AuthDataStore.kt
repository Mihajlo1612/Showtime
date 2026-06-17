package org.mihajlo1612.showtime.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class AuthDataStore(private val dataStore: DataStore<Preferences>) {

    private val tokenKey = stringPreferencesKey("auth_token")

    suspend fun saveToken(token: String) {
        dataStore.edit { it[tokenKey] = token }
    }

    suspend fun getToken(): String? =
        dataStore.data.map { it[tokenKey] }.firstOrNull()

    suspend fun clearToken() {
        dataStore.edit { it.remove(tokenKey) }
    }

    fun observeToken(): Flow<String?> =
        dataStore.data.map { it[tokenKey] }
}
