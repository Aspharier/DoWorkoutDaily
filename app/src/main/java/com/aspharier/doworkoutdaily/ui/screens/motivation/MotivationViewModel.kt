package com.aspharier.doworkoutdaily.ui.screens.motivation

import androidx.lifecycle.ViewModel
import com.aspharier.doworkoutdaily.data.MotivationQuote
import com.aspharier.doworkoutdaily.data.motivationQuotes
import com.aspharier.doworkoutdaily.data.workoutImageUrls
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

data class MotivationUiState(
    val quote: MotivationQuote = motivationQuotes[0],
    val imageUrl: String = workoutImageUrls[0],
    val quoteIndex: Int = 0
)

class MotivationViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(getInitialState())
    val uiState: StateFlow<MotivationUiState> = _uiState.asStateFlow()

    private fun getInitialState(): MotivationUiState {
        val dayOfYear = LocalDate.now().dayOfYear
        val quoteIndex = dayOfYear % motivationQuotes.size
        val imageIndex = dayOfYear % workoutImageUrls.size
        return MotivationUiState(
            quote = motivationQuotes[quoteIndex],
            imageUrl = workoutImageUrls[imageIndex],
            quoteIndex = quoteIndex
        )
    }

    fun refreshQuote() {
        val nextIndex = (_uiState.value.quoteIndex + 1) % motivationQuotes.size
        val nextImageIndex = nextIndex % workoutImageUrls.size
        _uiState.value = MotivationUiState(
            quote = motivationQuotes[nextIndex],
            imageUrl = workoutImageUrls[nextImageIndex],
            quoteIndex = nextIndex
        )
    }
}
