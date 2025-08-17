package com.example.hackathon.data.remote.client

import com.example.hackathon.data.remote.api.ApiService
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private fun logging(): HttpLoggingInterceptor =
    HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY   // URL/헤더/바디까지 모두 출력
    }


object ApiClient {
    private const val BASE_URL = "https://emodapp.link/"


    private val okHttp = OkHttpClient.Builder()
        // .addInterceptor { chain -> chain.proceed(chain.request().newBuilder()
        //     .addHeader("Authorization", "Bearer ...").build()) }
        .addInterceptor(logging())
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

    fun createApi(jwtProvider: suspend () -> String?): ApiService {
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(jwtProvider)) // ✅ 추가
            .addInterceptor(logging())
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

class AuthInterceptor(
    private val jwtProvider: suspend () -> String?
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        // 커스텀 플래그 확인
        val requireAuth = original.header("Require-Auth") != null

        // 플래그 제거 (실제 서버로 안 나가게)
        val reqBuilder = original.newBuilder().removeHeader("Require-Auth")

        if (requireAuth) {
            val token = runBlocking { jwtProvider() }
            if (!token.isNullOrBlank()) {
                reqBuilder.addHeader("Authorization", "Bearer $token")
            }
        }
        return chain.proceed(reqBuilder.build())
    }
}
