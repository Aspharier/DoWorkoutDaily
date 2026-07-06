package com.aspharier.doworkoutdaily.ui.screens.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aspharier.doworkoutdaily.data.preferences.AppPreferences
import com.aspharier.doworkoutdaily.notifications.AlarmScheduler
import com.aspharier.doworkoutdaily.ui.theme.ThemeMode
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.AMOLED_BLACK,
    val reminderEnabled: Boolean = false,
    val reminderHour: Int = 7,
    val reminderMinute: Int = 0,
    val alarmToneUri: String = ""
)

class SettingsViewModel(
    application: Application,
    private val preferences: AppPreferences
) : AndroidViewModel(application) {

    private val alarmScheduler = AlarmScheduler(application)

    val uiState: StateFlow<SettingsUiState> = combine(
        preferences.themeMode,
        preferences.reminderEnabled,
        preferences.reminderHour,
        preferences.reminderMinute,
        preferences.alarmToneUri
    ) { theme, enabled, hour, minute, tone ->
        SettingsUiState(
            themeMode = theme,
            reminderEnabled = enabled,
            reminderHour = hour,
            reminderMinute = minute,
            alarmToneUri = tone
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            preferences.setThemeMode(mode)
        }
    }

    fun setReminderEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferences.setReminderEnabled(enabled)
            if (enabled) {
                val state = uiState.value
                alarmScheduler.scheduleAlarm(
                    state.reminderHour,
                    state.reminderMinute,
                    state.alarmToneUri
                )
            } else {
                alarmScheduler.cancelAlarm()
            }
        }
    }

    fun setReminderTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            preferences.setReminderTime(hour, minute)
            val state = uiState.value
            if (state.reminderEnabled) {
                alarmScheduler.scheduleAlarm(hour, minute, state.alarmToneUri)
            }
        }
    }

    fun setAlarmToneUri(uri: String) {
        viewModelScope.launch {
            preferences.setAlarmToneUri(uri)
            val state = uiState.value
            if (state.reminderEnabled) {
                alarmScheduler.scheduleAlarm(state.reminderHour, state.reminderMinute, uri)
            }
        }
    }

    class Factory(
        private val application: Application,
        private val preferences: AppPreferences
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(application, preferences) as T
        }
    }
}
