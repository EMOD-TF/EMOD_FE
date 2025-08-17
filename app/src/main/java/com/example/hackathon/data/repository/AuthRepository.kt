package com.example.hackathon.data.repository

import android.content.Context
import com.example.hackathon.data.local.datastore.AuthDataStore
import com.example.hackathon.data.remote.client.ApiClient
import com.example.hackathon.data.remote.dto.auth.AuthSignupRequest
import com.example.hackathon.domain.model.UserSession

class AuthRepository(
    context: Context
) {
    private val api = ApiClient.api
    private val store = AuthDataStore(context)

    suspend fun signup(deviceCode: String): Result<UserSession> {
        return try {
            val res = api.signup(AuthSignupRequest(deviceCode))
            if (res.isSuccessful) {
                val body = res.body() ?: return Result.failure(IllegalStateException("Empty body"))
                val session = UserSession(
                    authId = body.authId,
                    deviceCode = body.deviceCode,
                    jwt = body.jwt,
                    profileCompleted = body.profileCompleted
                )
                // 로컬 저장
                store.save(jwt = body.jwt, deviceCode = body.deviceCode, profileCompleted = body.profileCompleted)
                Result.success(session)
            } else {
                Result.failure(IllegalStateException("HTTP ${res.code()}: ${res.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
