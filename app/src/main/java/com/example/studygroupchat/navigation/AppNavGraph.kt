package com.example.studygroupchat.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.compose.ui.Modifier
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.studygroupchat.ui.screen.home.HomeScreen
import com.example.studygroupchat.ui.screen.login.LoginScreen
import com.example.studygroupchat.ui.screen.register.RegisterScreen
import androidx.compose.ui.platform.LocalContext
import com.example.studygroupchat.ui.screen.SplashScreen
import com.example.studygroupchat.ui.screen.OnboardingScreen

//@Composable
//fun AppNavGraph(navController: NavHostController = rememberNavController()) {
//    NavHost(navController = navController, startDestination = "login") {
//        composable("login") {
//            LoginScreen(navController)
//        }
//        composable("register") {
//            RegisterScreen(navController)
//        }
//    }
//}
@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
    val token = sharedPref.getString("token", null)
    val startDestination = if (token != null) "home" else "splash"

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable("splash") {
            SplashScreen(navController)
        }
        composable("onboarding") {
            OnboardingScreen(navController)
        }
        composable("login") {
            LoginScreen(navController)
        }
        composable("register") {
            RegisterScreen(navController)
        }
        composable("home") {
            HomeScreen()
        }
    }
}
