package com.example.hackathon.data.remote.api

import com.example.hackathon.data.remote.dto.proceed.ProceedRequest
import com.example.hackathon.data.remote.dto.proceed.ProceedResponse
import com.example.hackathon.data.remote.dto.summary.SummaryRequest
import com.example.hackathon.data.remote.dto.summary.SummaryResponse
import com.example.hackathon.data.remote.dto.auth.AuthSignupRequest
import com.example.hackathon.data.remote.dto.auth.AuthSignupResponse
import com.example.hackathon.data.remote.dto.profile.ProfileRequest
import com.example.hackathon.data.remote.dto.profile.ProfileResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {
    @POST("api/v1/profile")
    @Headers("Require-Auth: true")
    suspend fun postProfile(@Body req: ProfileRequest): Response<ProfileResponse>

    @POST("api/v1/auth/signup")
    suspend fun signup(@Body req: AuthSignupRequest): Response<AuthSignupResponse>

    @POST("api/v1/convo/proceed")
    @Headers("Require-Auth: true") // ✅ 토큰 필요시 추가
    suspend fun proceed(
        @Body request: ProceedRequest
    ): Response<ProceedResponse>

    @POST("api/v1/summary/today")
    @Headers("Require-Auth: true") // ✅ 토큰 필요시 추가
    suspend fun summary(
        @Body request: SummaryRequest
    ): Response<SummaryResponse>
}

