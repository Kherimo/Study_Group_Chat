package com.example.studygroupchat.repository

import com.example.studygroupchat.api.GeminiApiService
import com.example.studygroupchat.model.ai.GeminiContent
import com.example.studygroupchat.model.ai.GeminiPart
import com.example.studygroupchat.model.ai.GeminiRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AiChatRepository(
    private val apiService: GeminiApiService,
    private val apiKey: String
) {
    suspend fun ask(question: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(parts = listOf(GeminiPart(text = question)))
                )
            )
            val response = apiService.generateContent(apiKey, request)
            val answer = response.candidates?.firstOrNull()
                ?.content?.parts?.firstOrNull()?.text ?: ""
            Result.success(answer)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}