package com.example.hackathon.data.remote.api

import com.example.hackathon.data.remote.dto.LoginRequest
import com.example.hackathon.data.remote.dto.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("login")
    suspend fun login(@Body req: LoginRequest): Response<LoginResponse>
}
