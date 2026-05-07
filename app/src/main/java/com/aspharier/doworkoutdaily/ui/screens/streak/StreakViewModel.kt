package com.aspharier.doworkoutdaily.ui.screens.streak

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aspharier.doworkoutdaily.data.model.DailySelfie
import com.aspharier.doworkoutdaily.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

data class StreakUiState(
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val totalDays: Int = 0,
    val totalWorkouts: Int = 0,
    val heatmapData: Map<LocalDate, Int> = emptyMap(),
    val selfiesData: Map<LocalDate, String> = emptyMap()
)

class StreakViewModel(private val repository: WorkoutRepository) : ViewModel() {

    val uiState: StateFlow<StreakUiState> = combine(
        repository.getCurrentStreak(),
        repository.getLongestStreak(),
        repository.getTotalWorkoutDays(),
        repository.getTotalWorkouts(),
        repository.getAllWorkoutDatesWithCounts(),
        repository.getAllSelfies()
    ) { args: Array<Any?> ->
        val streak = args[0] as Int
        val longest = args[1] as Int
        val totalDays = args[2] as Int
        val totalWorkouts = args[3] as Int
        val heatmap = args[4] as Map<LocalDate, Int>
        val selfies = args[5] as List<DailySelfie>

        val selfiesMap = selfies.mapNotNull {
            val date = runCatching { LocalDate.parse(it.date) }.getOrNull()
            if (date != null) date to it.imagePath else null
        }.toMap()

        StreakUiState(
            currentStreak = streak,
            longestStreak = longest,
            totalDays = totalDays,
            totalWorkouts = totalWorkouts,
            heatmapData = heatmap,
            selfiesData = selfiesMap
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StreakUiState()
    )

    fun saveSelfie(date: LocalDate, imagePath: String) {
        viewModelScope.launch {
            repository.saveSelfie(date, imagePath)
        }
    }

    class Factory(private val repository: WorkoutRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return StreakViewModel(repository) as T
        }
    }
}
