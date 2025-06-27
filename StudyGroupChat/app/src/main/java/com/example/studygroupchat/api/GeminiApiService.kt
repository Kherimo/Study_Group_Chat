package com.example.studygroupchat.api

import com.example.studygroupchat.model.ai.GeminiRequest
import com.example.studygroupchat.model.ai.GeminiResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface GeminiApiService {
    @POST("v1beta/models/gemini-2.5-flash-lite-preview-06-17:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}