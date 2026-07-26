package com.aspharier.doworkoutdaily.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.aspharier.doworkoutdaily.data.preferences.AppPreferences
import com.aspharier.doworkoutdaily.ui.components.BottomNavBar
import com.aspharier.doworkoutdaily.ui.screens.*
import com.aspharier.doworkoutdaily.ui.theme.GrindTheme
import com.aspharier.doworkoutdaily.viewmodel.WorkoutViewModel

@Composable
fun AppNavigation(
    preferences: AppPreferences,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val workoutViewModel: WorkoutViewModel = viewModel()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

    val currentStreak by workoutViewModel.currentStreak.collectAsState()
    val todayEntries by workoutViewModel.todayEntries.collectAsState()
    val weekSessions by workoutViewModel.weekSessions.collectAsState()
    val yesterdayEntries by workoutViewModel.yesterdayEntries.collectAsState()
    val totalKm by workoutViewModel.totalKm.collectAsState()
    val allSessions by workoutViewModel.allSessions.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(GrindTheme.colors.cream)
    ) {
        // Screen Content (Scrolls edge-to-edge behind floating tab bar)
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.fillMaxSize(),
            enterTransition = { fadeIn(tween(200)) },
            exitTransition = { fadeOut(tween(200)) },
            popEnterTransition = { fadeIn(tween(200)) },
            popExitTransition = { fadeOut(tween(200)) }
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    currentStreak = currentStreak,
                    weekSessions = weekSessions,
                    todayEntries = todayEntries,
                    yesterdayEntries = yesterdayEntries,
                    totalKm = totalKm,
                    onNavigateToAddWorkout = {
                        navController.navigate(Screen.AddWorkout.route)
                    }
                )
            }

            composable(Screen.AddWorkout.route) {
                AddWorkoutScreen(
                    viewModel = workoutViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.Progress.route) {
                ProgressScreen(
                    allSessions = allSessions,
                    getAllExerciseNames = { workoutViewModel.getAllExerciseNames() },
                    getEntriesByExercise = { name -> workoutViewModel.getEntriesByExercise(name) },
                    getEntriesForSession = { sessionId -> workoutViewModel.getEntriesForSession(sessionId) }
                )
            }
        }

        // Floating Bottom Navigation Bar Overlay
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            BottomNavBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(Screen.Home.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
