package com.example.studygroupchat.ui.screen.home

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import java.net.URL

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel(), navController: NavController) {
    val context = LocalContext.current
    var roomCode by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Study Room", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = roomCode,
            onValueChange = { roomCode = it },
            label = { Text("Room Code") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (roomCode.isNotBlank()) {
                    launchJitsiMeet(context, roomCode)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Join Room")
        }
    }
}

fun launchJitsiMeet(context: Context, roomCode: String) {
    try {
        val options = JitsiMeetConferenceOptions.Builder()
            .setServerURL(URL("https://meet.jit.si"))
            .setRoom(roomCode)
            .setAudioMuted(false)
            .setVideoMuted(false)
            .setFeatureFlag("prejoinpage.enabled", false)         // ❌ Bỏ màn hình xác nhận trước khi vào
            .setFeatureFlag("welcomepage.enabled", false)         // ❌ Bỏ trang chào mừng
            .setFeatureFlag("lobby-mode.enabled", false)          // ❌ Bỏ yêu cầu duyệt trước khi vào
            .setFeatureFlag("invite.enabled", false)              // ❌ Bỏ gửi lời mời
            .setFeatureFlag("security-options.enabled", false)    // ❌ Bỏ yêu cầu đặt mật khẩu
            .setFeatureFlag("video-share.enabled", false)         
            .setFeatureFlag("add-people.enabled", false)
            .setFeatureFlag("calendar.enabled", false)
            .setFeatureFlag("chat.enabled", true)
            .setFeatureFlag("recording.enabled", true)
            .setFeatureFlag("android.screensharing.enabled", true)
            .build()

        JitsiMeetActivity.launch(context, options)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
