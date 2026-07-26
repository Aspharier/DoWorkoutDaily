package com.aspharier.doworkoutdaily

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.aspharier.doworkoutdaily.data.preferences.AppPreferences
import com.aspharier.doworkoutdaily.navigation.AppNavigation
import com.aspharier.doworkoutdaily.notifications.NotificationHelper
import com.aspharier.doworkoutdaily.ui.theme.GrindTheme

class MainActivity : ComponentActivity() {

    private lateinit var preferences: AppPreferences

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            android.widget.Toast.makeText(
                this,
                "Notifications disabled. Workout reminders will not be shown.",
                android.widget.Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize preferences & notifications
        preferences = AppPreferences(this)
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
            GrindTheme {
                AppNavigation(preferences = preferences)
            }
        }
    }
}