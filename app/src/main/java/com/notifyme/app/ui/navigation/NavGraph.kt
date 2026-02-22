package com.notifyme.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.notifyme.app.ui.create.CreateReminderScreen
import com.notifyme.app.ui.home.HomeScreen

object Routes {
    const val HOME = "home"
    const val CREATE = "create"
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onCreateReminder = { navController.navigate(Routes.CREATE) }
            )
        }
        composable(Routes.CREATE) {
            CreateReminderScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
