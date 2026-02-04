package com.msis.adhittan.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msis.adhittan.data.model.BirthDay
import com.msis.adhittan.data.model.NawinDay
import com.msis.adhittan.data.repository.SessionRepository
import com.msis.adhittan.logic.NawinLogic
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class NawinUiState(
    val currentNawinDay: NawinDay? = null,
    val count: Int = 0,
    val index: Int = 0, // 0 to 8
    val isActive: Boolean = false,
    val isGoalReached: Boolean = false,
    val vibrationEnabled: Boolean = true
)

class NawinViewModel(
    private val sessionRepository: SessionRepository
) : ViewModel() {

    // Internal state for current count (since we don't have a progress map for Nawin yet, 
    // we can use a simpler approach or reuse the existing logic. 
    // To keep it separate, let's use its own key or just session-based count.)
    // Actually, prompt says "Update persistence... to save BirthDay and CurrentDayIndex".
    // It doesn't explicitly mention saving current count within the day, 
    // but for a good UX, we should save it.
    // I'll reuse the progress map from SessionRepository but with special keys or just index as day.
    
    val uiState: StateFlow<NawinUiState> = combine(
        sessionRepository.isNawinActive,
        sessionRepository.nawinIndex,
        sessionRepository.vibrationEnabled,
        sessionRepository.progressMap
    ) { active, index, vibration, progress ->
        if (active) {
            val sequence = NawinLogic.generateSequence()
            val currentDay = sequence.getOrNull(index)
            
            val count = progress[index] ?: 0

            NawinUiState(
                currentNawinDay = currentDay,
                count = count,
                index = index,
                isActive = true,
                isGoalReached = currentDay != null && count >= currentDay.mantraCount,
                vibrationEnabled = vibration
            )
        } else {
            NawinUiState(vibrationEnabled = vibration)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NawinUiState())

    fun increment() {
        val currentState = uiState.value
        if (currentState.isActive) {
            viewModelScope.launch {
                sessionRepository.updateProgress(currentState.index, currentState.count + 1)
            }
        }
    }

    fun nextDay() {
        val currentState = uiState.value
        if (currentState.isGoalReached && currentState.index < 80) {
            viewModelScope.launch {
                sessionRepository.updateNawinIndex(currentState.index + 1)
            }
        }
    }

    fun reset() {
        val currentState = uiState.value
        if (currentState.isActive) {
            viewModelScope.launch {
                sessionRepository.updateProgress(currentState.index, 0)
            }
        }
    }
}
