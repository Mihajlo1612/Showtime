package org.mihajlo1612.showtime.data.repository

import kotlinx.coroutines.flow.map
import org.mihajlo1612.showtime.data.local.datastore.AuthDataStore
import org.mihajlo1612.showtime.data.remote.api.AuthApi
import org.mihajlo1612.showtime.data.remote.model.LoginRequest
import org.mihajlo1612.showtime.data.remote.model.RegisterRequest
import org.mihajlo1612.showtime.domain.model.User
import org.mihajlo1612.showtime.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val authDataStore: AuthDataStore,
) : AuthRepository {

    override suspend fun login(username: String, password: String): User {
        val response = authApi.login(LoginRequest(username, password))
        authDataStore.saveToken(response.accessToken)
        return User(
            id = response.user.id,
            username = response.user.username,
            fullName = response.user.fullName,
        )
    }

    override suspend fun register(fullName: String, username: String, password: String): User {
        val response = authApi.register(RegisterRequest(fullName = fullName, username = username, password = password))
        authDataStore.saveToken(response.accessToken)
        return User(
            id = response.user.id,
            username = response.user.username,
            fullName = response.user.fullName,
        )
    }

    override suspend fun logout() {
        authDataStore.clearToken()
    }

    override suspend fun getToken(): String? = authDataStore.getToken()

    override fun observeLoginState() = authDataStore.observeToken().map { it != null }

    override suspend fun getMe(): User {
        val r = authApi.getMe()
        return User(id = r.id, username = r.username, fullName = r.fullName)
    }
}