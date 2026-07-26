package com.aspharier.doworkoutdaily.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object AddWorkout : Screen("add_workout")
    data object Progress : Screen("progress")
}
