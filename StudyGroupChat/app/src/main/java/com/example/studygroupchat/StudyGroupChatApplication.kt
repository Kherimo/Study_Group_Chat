package com.example.studygroupchat

import android.app.Application
import com.example.studygroupchat.api.ApiConfig

class StudyGroupChatApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ApiConfig.init(this)
    }
}