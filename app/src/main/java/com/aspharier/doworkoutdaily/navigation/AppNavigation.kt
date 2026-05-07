package com.aspharier.doworkoutdaily.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.aspharier.doworkoutdaily.data.preferences.AppPreferences
import com.aspharier.doworkoutdaily.data.repository.WorkoutRepository
import com.aspharier.doworkoutdaily.ui.screens.home.HomeScreen
import com.aspharier.doworkoutdaily.ui.screens.log.LogWorkoutScreen
import com.aspharier.doworkoutdaily.ui.screens.motivation.MotivationScreen
import com.aspharier.doworkoutdaily.ui.screens.settings.SettingsScreen
import com.aspharier.doworkoutdaily.ui.screens.streak.StreakScreen

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    repository: WorkoutRepository,
    preferences: AppPreferences,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    val bottomNavItems = listOf(
        BottomNavItem(Screen.Home, "Home", Icons.Rounded.Home, Icons.Rounded.Home),
        BottomNavItem(Screen.LogWorkout, "Log", Icons.Rounded.Add, Icons.Rounded.Add),
        BottomNavItem(Screen.Streak, "Streak", Icons.Rounded.LocalFireDepartment, Icons.Rounded.LocalFireDepartment),
        BottomNavItem(Screen.Motivation, "Motivate", Icons.Rounded.AutoAwesome, Icons.Rounded.AutoAwesome),
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = bottomNavItems.any { item ->
        currentDestination?.hierarchy?.any { it.route == item.screen.route } == true
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    tonalElevation = 0.dp
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.route == item.screen.route
                        } == true

                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.icon,
                                    contentDescription = item.label
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues),
            enterTransition = {
                fadeIn(animationSpec = tween(300)) + slideInHorizontally(
                    initialOffsetX = { 30 },
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(300))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(300)) + slideInHorizontally(
                    initialOffsetX = { -30 },
                    animationSpec = tween(300)
                )
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(300))
            }
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    repository = repository,
                    onNavigateToLog = {
                        navController.navigate(Screen.LogWorkout.route)
                    },
                    onNavigateToSettings = {
                        navController.navigate(Screen.Settings.route)
                    }
                )
            }
            composable(Screen.LogWorkout.route) {
                LogWorkoutScreen(
                    repository = repository,
                    onWorkoutSaved = {
                        navController.popBackStack()
                    }
                )
            }
            composable(Screen.Streak.route) {
                StreakScreen(repository = repository)
            }
            composable(Screen.Motivation.route) {
                MotivationScreen()
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    preferences = preferences,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
