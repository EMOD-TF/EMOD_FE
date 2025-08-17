package com.example.hackathon.data.remote.client

import com.example.hackathon.data.remote.api.ApiService
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val BASE_URL = "https://emodapp.link/"


    private val okHttp = OkHttpClient.Builder()
        // .addInterceptor { chain -> chain.proceed(chain.request().newBuilder()
        //     .addHeader("Authorization", "Bearer ...").build()) }
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

class AuthInterceptor(
    private val jwtProvider: suspend () -> String?
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request()
        val token = runBlocking { jwtProvider() }
        val newReq = if (!token.isNullOrBlank()) {
            req.newBuilder().addHeader("Authorization", "Bearer $token").build()
        } else req
        return chain.proceed(newReq)
    }
}