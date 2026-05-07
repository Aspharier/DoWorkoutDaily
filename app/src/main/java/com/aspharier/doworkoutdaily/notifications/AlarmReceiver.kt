package com.aspharier.doworkoutdaily.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.aspharier.doworkoutdaily.data.preferences.AppPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val toneUri = intent.getStringExtra(AlarmScheduler.EXTRA_TONE_URI)
        val notificationHelper = NotificationHelper(context)
        notificationHelper.showReminderNotification(toneUri)

        // Reschedule for next day
        val preferences = AppPreferences(context)
        val scheduler = AlarmScheduler(context)

        runBlocking {
            val hour = preferences.reminderHour.first()
            val minute = preferences.reminderMinute.first()
            val tone = preferences.alarmToneUri.first()
            scheduler.scheduleAlarm(hour, minute, tone)
        }
    }
}
