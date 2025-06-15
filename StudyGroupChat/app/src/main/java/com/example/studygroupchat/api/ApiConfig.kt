package com.example.studygroupchat.api

import android.content.Context
import android.util.Log
import com.example.studygroupchat.utils.TokenPreferences
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {
    // Thay đổi BASE_URL tại đây
    private const val BASE_URL = "https://predictions-conversations-dl-savings.trycloudflare.com/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private fun getClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))
            .addInterceptor(loggingInterceptor)
            .build()
    }

    private fun getRetrofit(context: Context): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getClient(context))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private lateinit var context: Context

    fun init(context: Context) {
        this.context = context.applicationContext
    }

    val authApiService: AuthApiService by lazy {
        getRetrofit(context).create(AuthApiService::class.java)
    }

    val roomApiService: RoomApiService by lazy {
        getRetrofit(context).create(RoomApiService::class.java)
    }

    val roomMessageApiService: RoomMessageApiService by lazy {
        getRetrofit(context).create(RoomMessageApiService::class.java)
    }

    val userApiService: UserApiService by lazy {
        getRetrofit(context).create(UserApiService::class.java)
    }
}

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val tokenPreferences = TokenPreferences(context)
        val accessToken = runBlocking { tokenPreferences.getAccessToken() }
        Log.d("AuthInterceptor", "Access token: $accessToken")

        val originalRequest = chain.request()
        val newRequest = if (!accessToken.isNullOrEmpty()) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(newRequest)
    }
}