package com.example.hackathon.data.remote.api

import com.example.hackathon.data.remote.dto.LoginRequest
import com.example.hackathon.data.remote.dto.LoginResponse
import com.example.hackathon.data.remote.dto.ProceedRequest
import com.example.hackathon.data.remote.dto.ProceedResponse
import com.example.hackathon.data.remote.dto.SummaryRequest
import com.example.hackathon.data.remote.dto.SummaryResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("login")
    suspend fun login(@Body req: LoginRequest): Response<LoginResponse>

    @POST("convo/proceed")
    fun proceed(@Body request: ProceedRequest): Call<ProceedResponse>

    @POST("summary/today")
    fun summary(@Body request: SummaryRequest): Call<SummaryResponse>
}
