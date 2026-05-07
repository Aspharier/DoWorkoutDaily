package com.aspharier.doworkoutdaily.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.aspharier.doworkoutdaily.data.preferences.AppPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val preferences = AppPreferences(context)
            val scheduler = AlarmScheduler(context)

            runBlocking {
                val enabled = preferences.reminderEnabled.first()
                if (enabled) {
                    val hour = preferences.reminderHour.first()
                    val minute = preferences.reminderMinute.first()
                    val tone = preferences.alarmToneUri.first()
                    scheduler.scheduleAlarm(hour, minute, tone)
                }
            }
        }
    }
}
