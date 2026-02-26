package com.notifyme.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.notifyme.app.ui.create.CreateReminderScreen
import com.notifyme.app.ui.home.HomeScreen

object Routes {
    const val HOME = "home"
    const val CREATE = "create"
    const val EDIT = "edit/{reminderId}"

    fun editRoute(id: Long) = "edit/$id"
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onCreateReminder = { navController.navigate(Routes.CREATE) },
                onEditReminder = { id -> navController.navigate(Routes.editRoute(id)) }
            )
        }
        composable(Routes.CREATE) {
            CreateReminderScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            Routes.EDIT,
            arguments = listOf(navArgument("reminderId") { type = NavType.LongType })
        ) { backStackEntry ->
            val reminderId = backStackEntry.arguments?.getLong("reminderId")
            CreateReminderScreen(
                onNavigateBack = { navController.popBackStack() },
                reminderId = reminderId
            )
        }
    }
}
