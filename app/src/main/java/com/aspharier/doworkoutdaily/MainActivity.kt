package com.aspharier.doworkoutdaily

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import com.aspharier.doworkoutdaily.data.local.WorkoutDatabase
import com.aspharier.doworkoutdaily.data.preferences.AppPreferences
import com.aspharier.doworkoutdaily.data.repository.WorkoutRepository
import com.aspharier.doworkoutdaily.navigation.AppNavigation
import com.aspharier.doworkoutdaily.notifications.NotificationHelper
import com.aspharier.doworkoutdaily.ui.theme.DoWorkoutDailyTheme
import com.aspharier.doworkoutdaily.ui.theme.ThemeMode

class MainActivity : ComponentActivity() {

    private lateinit var preferences: AppPreferences
    private lateinit var repository: WorkoutRepository

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* Permission result handled */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize dependencies
        val database = WorkoutDatabase.getInstance(this)
        repository = WorkoutRepository(database.workoutDao(), database.selfieDao())
        preferences = AppPreferences(this)

        // Create notification channel
        NotificationHelper(this)

        // Request notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            val themeMode by preferences.themeMode.collectAsState(initial = ThemeMode.AMOLED_BLACK)

            DoWorkoutDailyTheme(themeMode = themeMode) {
                AppNavigation(
                    repository = repository,
                    preferences = preferences
                )
            }
        }
    }
}