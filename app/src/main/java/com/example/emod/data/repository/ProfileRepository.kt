// data/repository/ProfileRepository.kt
package com.example.emod.data.repository

import android.content.Context
import com.example.emod.data.local.datastore.AuthDataStore
import com.example.emod.data.remote.client.ApiClient
import com.example.emod.data.remote.dto.profile.ProfileRequest
import com.example.emod.data.remote.dto.profile.ProfileResponse
import com.example.emod.ui.signUp.SignupViewModel
import kotlinx.coroutines.flow.firstOrNull

class ProfileRepository(private val ctx: Context) {
    private val store = AuthDataStore(ctx)

    // ✅ 토큰은 인터셉터가 자동으로 붙임
    private val api by lazy {
        ApiClient.createApi { store.jwtFlow.firstOrNull() }
    }

    suspend fun postProfile(vm: SignupViewModel): Result<ProfileResponse> {
        val req = ProfileRequest(
            name = vm.name ?: return Result.failure(IllegalArgumentException("name null")),
            birthYear = vm.birthYear ?: return Result.failure(IllegalArgumentException("birthYear null")),
            birthMonth = vm.birthMonth ?: return Result.failure(IllegalArgumentException("birthMonth null")),
            gender = when (vm.gender) {
                SignupViewModel.Gender.MALE -> "MALE"
                SignupViewModel.Gender.FEMALE -> "FEMALE"
                else -> return Result.failure(IllegalArgumentException("gender null"))
            },
            q1 = vm.q1 ?: return Result.failure(IllegalArgumentException("q1 null")),
            q2 = vm.q2 ?: return Result.failure(IllegalArgumentException("q2 null")),
            learningPlace = vm.learningPlace?.name
                ?: return Result.failure(IllegalArgumentException("learningPlace null"))
        )

        return try {
            val res = api.postProfile(req)
            if (res.isSuccessful) {
                val body = res.body() ?: return Result.failure(IllegalStateException("Empty body"))
                // ✅ 프로필 생성이 성공했다면 로컬에서도 완료 처리 보장
                store.setProfileCompleted(true)
                Result.success(body)
            } else {
                Result.failure(IllegalStateException("HTTP ${res.code()}: ${res.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
