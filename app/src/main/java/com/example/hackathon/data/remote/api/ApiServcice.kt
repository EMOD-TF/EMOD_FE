package com.example.hackathon.data.remote.api

import com.example.emod.data.remote.dto.auth.AuthSignupRequest
import com.example.emod.data.remote.dto.auth.AuthSignupResponse
import com.example.emod.data.remote.dto.proceed.ProceedRequest
import com.example.emod.data.remote.dto.proceed.ProceedResponse
import com.example.emod.data.remote.dto.profile.ProfileRequest
import com.example.emod.data.remote.dto.profile.ProfileResponse
import com.example.emod.data.remote.dto.summary.SummaryRequest
import com.example.emod.data.remote.dto.summary.SummaryResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {
    @POST("api/v1/profile")
    @Headers("Require-Auth: true")
    suspend fun postProfile(@Body req: ProfileRequest): Response<ProfileResponse>

    @POST("api/v1/auth/signup")
    suspend fun signup(@Body req: AuthSignupRequest): Response<AuthSignupResponse>
    
    @POST("convo/proceed")
    fun proceed(@Body request: ProceedRequest): Call<ProceedResponse>

    @POST("summary/today")
    fun summary(@Body request: SummaryRequest): Call<SummaryResponse>
}

