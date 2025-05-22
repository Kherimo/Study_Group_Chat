package com.example.studygroupchat.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext

// Dữ liệu mẫu cho onboarding
val onboardingItems = listOf(
    OnboardingItem("Chào mừng đến với StudyGroupChat", "Kết nối bạn bè, học tập hiệu quả", /*R.drawable.onboard1*/ 0),
    OnboardingItem("Tạo nhóm học tập", "Thảo luận, chia sẻ kiến thức mọi lúc", /*R.drawable.onboard2*/ 0),
    OnboardingItem("Bảo mật & tiện lợi", "Đăng nhập an toàn, sử dụng dễ dàng", /*R.drawable.onboard3*/ 0)
)

data class OnboardingItem(val title: String, val desc: String, val imageRes: Int)

@Composable
fun OnboardingScreen(navController: NavController) {
    val pagerState = rememberPagerState(pageCount = { onboardingItems.size })
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(state = pagerState) { page ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Image(painter = painterResource(id = onboardingItems[page].imageRes), contentDescription = null, modifier = Modifier.size(180.dp))
                Text(onboardingItems[page].title, style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(12.dp))
                Text(onboardingItems[page].desc, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(32.dp))
                if (page == onboardingItems.lastIndex) {
                    Button(onClick = {
                        // Lưu cờ onboarding_shown vào SharedPreferences
                        val sharedPref = context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
                        sharedPref.edit().putBoolean("onboarding_shown", true).apply()
                        navController.navigate("login") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    }) {
                        Text("Bắt đầu")
                    }
                } else {
                    Button(onClick = {
                        scope.launch { pagerState.animateScrollToPage(page + 1) }
                    }) {
                        Text("Tiếp tục")
                    }
                }
            }
        }
    }
} 