package com.aspharier.doworkoutdaily.ui.screens.log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aspharier.doworkoutdaily.data.model.WorkoutLog
import com.aspharier.doworkoutdaily.data.model.WorkoutType
import com.aspharier.doworkoutdaily.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class LogWorkoutUiState(
    val selectedType: WorkoutType? = null,
    val duration: Int = 30,
    val notes: String = "",
    val sets: String = "",
    val reps: String = "",
    val weight: String = "",
    val showOptionalFields: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val showTypeError: Boolean = false
)

class LogWorkoutViewModel(private val repository: WorkoutRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LogWorkoutUiState())
    val uiState: StateFlow<LogWorkoutUiState> = _uiState.asStateFlow()

    fun selectType(type: WorkoutType) {
        _uiState.value = _uiState.value.copy(selectedType = type, showTypeError = false)
    }

    fun setDuration(duration: Int) {
        _uiState.value = _uiState.value.copy(duration = duration.coerceIn(1, 300))
    }

    fun setNotes(notes: String) {
        _uiState.value = _uiState.value.copy(notes = notes)
    }

    fun setSets(sets: String) {
        _uiState.value = _uiState.value.copy(sets = sets)
    }

    fun setReps(reps: String) {
        _uiState.value = _uiState.value.copy(reps = reps)
    }

    fun setWeight(weight: String) {
        _uiState.value = _uiState.value.copy(weight = weight)
    }

    fun toggleOptionalFields() {
        _uiState.value = _uiState.value.copy(showOptionalFields = !_uiState.value.showOptionalFields)
    }

    fun saveWorkout(onSaved: () -> Unit) {
        val state = _uiState.value
        if (state.selectedType == null) {
            _uiState.value = state.copy(showTypeError = true)
            return
        }

        _uiState.value = state.copy(isSaving = true)

        viewModelScope.launch {
            val workout = WorkoutLog(
                date = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
                workoutType = state.selectedType.name,
                durationMinutes = state.duration,
                notes = state.notes,
                sets = state.sets.toIntOrNull(),
                reps = state.reps.toIntOrNull(),
                weightKg = state.weight.toFloatOrNull()
            )
            repository.logWorkout(workout)
            _uiState.value = _uiState.value.copy(isSaving = false, isSaved = true)
            onSaved()
        }
    }

    class Factory(private val repository: WorkoutRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LogWorkoutViewModel(repository) as T
        }
    }
}
