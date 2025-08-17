package com.example.hackathon.data.remote.api

import com.example.hackathon.data.remote.dto.auth.AuthSignupRequest
import com.example.hackathon.data.remote.dto.auth.AuthSignupResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/v1/auth/signup")
    suspend fun signup(@Body req: AuthSignupRequest): Response<AuthSignupResponse>
}
