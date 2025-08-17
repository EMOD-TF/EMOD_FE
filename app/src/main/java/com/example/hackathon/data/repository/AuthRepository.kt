// data/repository/AuthRepository.kt
package com.example.hackathon.data.repository

import android.content.Context
import com.example.hackathon.data.local.datastore.AuthDataStore
import com.example.hackathon.data.remote.client.ApiClient
import com.example.hackathon.data.remote.dto.auth.AuthSignupRequest
import com.example.hackathon.domain.model.UserSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class AuthRepository(private val ctx: Context) {
    private val store = AuthDataStore(ctx)
    private val openApi = ApiClient.api

    suspend fun signup(deviceCode: String): Result<UserSession> = withContext(Dispatchers.IO) {
        try {
            val res = openApi.signup(AuthSignupRequest(deviceCode))
            if (!res.isSuccessful) {
                return@withContext Result.failure(IllegalStateException("HTTP ${res.code()}: ${res.errorBody()?.string()}"))
            }
            val body = res.body() ?: return@withContext Result.failure(IllegalStateException("Empty body"))

            val session = UserSession(
                authId = body.authId,
                deviceCode = body.deviceCode,
                jwt = body.jwt,
                profileCompleted = body.profileCompleted
            )

            // ✅ DataStore에 저장
            store.save(jwt = body.jwt, deviceCode = body.deviceCode, profileCompleted = body.profileCompleted)
            Result.success(session)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 필요 시: 현재 저장된 토큰 읽기
    suspend fun getJwt(): String? = store.jwtFlow.firstOrNull()
}
