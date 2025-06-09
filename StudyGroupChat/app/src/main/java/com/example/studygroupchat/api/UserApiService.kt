package com.example.studygroupchat.api

import com.example.studygroupchat.model.user.User
import retrofit2.Response
import retrofit2.http.GET


interface UserApiService {
    @GET("api/users/me")
    suspend fun getCurrentUser(): Response<User>

}