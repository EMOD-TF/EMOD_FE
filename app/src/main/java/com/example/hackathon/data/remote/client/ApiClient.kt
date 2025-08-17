package com.example.hackathon.data.remote.client

import com.example.hackathon.data.remote.api.ApiService
import okhttp3.OkHttpClient
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
