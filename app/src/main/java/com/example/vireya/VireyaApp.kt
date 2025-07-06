package com.example.vireya

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.compose.*

@Composable
fun VireyaApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination: NavDestination? = navBackStackEntry?.destination

    val bottomBarRoutes = setOf("upload", "calendar", "quiz", "chatbot", "profile")
    val showBottomBar = bottomBarRoutes.contains(currentDestination?.route)

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "splash",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("splash") { SplashScreen(navController = navController) }
            composable("welcome") { WelcomeScreen(navController = navController) }
            composable("login") { LoginScreen(navController = navController) }
            composable("signup") { SignUpScreen(navController = navController) }
            composable("GenQuiz") { GenQuiz(navController = navController) }
            composable("VitQuiz") { VitQuiz(navController = navController) }
            composable("HairQuiz") { HairQuiz(navController = navController) }
            composable("DietQuiz") { DietQuiz(navController = navController) }
            composable("ImmQuiz") { ImmQuiz(navController = navController) }
            composable("MentalQuiz") { MentalQuiz(navController = navController) }

            composable("upload") {
                HomeMenuScreen(
                    navController = navController,
                    onUploadClick = {
                        println("Upload clicked")
                    }
                )
            }
            composable("calendar") {
                CalendarScreen(navController = navController)
            }
            composable("quiz") {
                QuizSectionScreen(
                    navController = navController
                )
            }
            composable("chatbot") {
                ChatbotScreen(navController = navController)
            }
            composable("profile") {
                ProfileScreen(navController = navController)
            }
        }
    }
}
