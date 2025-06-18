package com.example.studygroupchat

import android.app.Application
import com.example.studygroupchat.api.ApiConfig
import com.example.studygroupchat.data.local.AppDatabase
import com.example.studygroupchat.repository.DataSyncManager
import com.example.studygroupchat.repository.RoomMessageRepository
import com.example.studygroupchat.repository.RoomRepository
import com.example.studygroupchat.repository.UserRepository

class StudyGroupChatApplication : Application() {
    lateinit var database: AppDatabase
    lateinit var syncManager: DataSyncManager
    override fun onCreate() {
        super.onCreate()
        ApiConfig.init(this)
        database = AppDatabase.getInstance(this)
        syncManager = DataSyncManager(
            UserRepository(ApiConfig.userApiService, database.userDao()),
            RoomRepository(ApiConfig.roomApiService, database.roomDao()),
            RoomMessageRepository(ApiConfig.roomMessageApiService, database.messageDao())
        )
    }
}