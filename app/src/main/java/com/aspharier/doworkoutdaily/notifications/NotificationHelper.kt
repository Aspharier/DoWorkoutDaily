package com.aspharier.doworkoutdaily.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.aspharier.doworkoutdaily.MainActivity
import com.aspharier.doworkoutdaily.R

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "workout_reminder"
        const val CHANNEL_NAME = "Workout Reminders"
        const val NOTIFICATION_ID = 2001
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Daily workout reminder notifications"
            enableVibration(true)
        }

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    fun showReminderNotification(toneUri: String? = null) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val fullScreenIntent = Intent(context, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(AlarmScheduler.EXTRA_TONE_URI, toneUri)
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            1,
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val motivationalMessages = listOf(
            "Time to get moving! 💪",
            "Your body is ready. Are you? 🔥",
            "Today's workout is waiting for you! 🏋️",
            "No excuses. Let's go! ⚡",
            "One workout closer to your goal! 🎯",
            "Rise and grind! Your streak depends on it! 🔥"
        )

        val message = motivationalMessages.random()

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("DoWorkoutDaily")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(pendingIntent)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, builder.build())
    }
}
