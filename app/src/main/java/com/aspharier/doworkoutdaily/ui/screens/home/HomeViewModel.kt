package com.aspharier.doworkoutdaily.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aspharier.doworkoutdaily.data.model.WorkoutLog
import com.aspharier.doworkoutdaily.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.*

data class HomeUiState(
    val currentStreak: Int = 0,
    val totalWorkouts: Int = 0,
    val thisWeekCount: Int = 0,
    val todayWorkouts: List<WorkoutLog> = emptyList(),
    val hasWorkedOutToday: Boolean = false
)

class HomeViewModel(private val repository: WorkoutRepository) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        repository.getCurrentStreak(),
        repository.getTotalWorkouts(),
        repository.getThisWeekCount(),
        repository.getWorkoutsForToday()
    ) { streak, total, weekCount, todayWorkouts ->
        HomeUiState(
            currentStreak = streak,
            totalWorkouts = total,
            thisWeekCount = weekCount,
            todayWorkouts = todayWorkouts,
            hasWorkedOutToday = todayWorkouts.isNotEmpty()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    class Factory(private val repository: WorkoutRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(repository) as T
        }
    }
}
