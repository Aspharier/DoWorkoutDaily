package com.aspharier.doworkoutdaily.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

class AppPreferences(private val context: Context) {

    companion object {
        private val USER_NAME = stringPreferencesKey("user_name")
        private val REMINDER_ENABLED = booleanPreferencesKey("reminder_enabled")
        private val REMINDER_HOUR = intPreferencesKey("reminder_hour")
        private val REMINDER_MINUTE = intPreferencesKey("reminder_minute")
        private val ALARM_TONE_URI = stringPreferencesKey("alarm_tone_uri")
        private val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
    }

    val userName: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[USER_NAME] ?: "champ"
    }

    val reminderEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[REMINDER_ENABLED] ?: false
    }

    val reminderHour: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[REMINDER_HOUR] ?: 7
    }

    val reminderMinute: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[REMINDER_MINUTE] ?: 0
    }

    val alarmToneUri: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[ALARM_TONE_URI] ?: ""
    }

    val isFirstLaunch: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[IS_FIRST_LAUNCH] ?: true
    }

    suspend fun setUserName(name: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_NAME] = name
        }
    }

    suspend fun setReminderEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[REMINDER_ENABLED] = enabled
        }
    }

    suspend fun setReminderTime(hour: Int, minute: Int) {
        context.dataStore.edit { prefs ->
            prefs[REMINDER_HOUR] = hour
            prefs[REMINDER_MINUTE] = minute
        }
    }

    suspend fun setAlarmToneUri(uri: String) {
        context.dataStore.edit { prefs ->
            prefs[ALARM_TONE_URI] = uri
        }
    }

    suspend fun setFirstLaunchDone() {
        context.dataStore.edit { prefs ->
            prefs[IS_FIRST_LAUNCH] = false
        }
    }
}
