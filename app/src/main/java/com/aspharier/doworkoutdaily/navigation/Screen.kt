package com.aspharier.doworkoutdaily.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object LogWorkout : Screen("log_workout")
    data object Streak : Screen("streak")
    data object Motivation : Screen("motivation")
    data object Settings : Screen("settings")
}
