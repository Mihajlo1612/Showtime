package org.mihajlo1612.showtime.domain.repository

import kotlinx.coroutines.flow.Flow
import org.mihajlo1612.showtime.domain.model.User

interface AuthRepository {
    suspend fun login(username: String, password: String): User
    suspend fun register(fullName: String, username: String, password: String): User
    suspend fun logout()
    suspend fun getToken(): String?
    fun observeLoginState(): Flow<Boolean>
    suspend fun getMe(): User
}