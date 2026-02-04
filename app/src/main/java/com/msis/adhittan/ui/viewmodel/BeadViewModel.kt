package com.msis.adhittan.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msis.adhittan.data.repository.LibraryRepository
import com.msis.adhittan.data.repository.SessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class BeadUiState(
    val count: Int = 0,
    val target: Int = 10,
    val currentDay: Int = 1,
    val chantText: String? = null,
    val isGoalReached: Boolean = false,
    val sessionActive: Boolean = false,
    val vibrationEnabled: Boolean = true
)

class BeadViewModel(
    private val sessionRepository: SessionRepository,
    private val libraryRepository: LibraryRepository
) : ViewModel() {

    private val _chantText = MutableStateFlow<String?>(null)
    
    val uiState: StateFlow<BeadUiState> = combine(
        sessionRepository.activeSession,
        _chantText,
        sessionRepository.vibrationEnabled
    ) { session, text, vibration ->
        if (session != null) {
            val count = session.dailyProgress[session.currentDay] ?: 0
            BeadUiState(
                count = count,
                target = session.targetCountPerDay,
                currentDay = session.currentDay,
                chantText = text,
                isGoalReached = count >= session.targetCountPerDay,
                sessionActive = true,
                vibrationEnabled = vibration
            )
        } else {
            BeadUiState(vibrationEnabled = vibration)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BeadUiState())

    private var loadedChantId: String? = null

    init {
        viewModelScope.launch {
            sessionRepository.activeSession.collect { session ->
                if (session != null && session.chantId != loadedChantId) {
                    loadChantText(session.chantId)
                }
            }
        }
    }

    private fun loadChantText(chantId: String) {
        viewModelScope.launch {
            val text = libraryRepository.getContent(chantId)
            _chantText.value = text
            loadedChantId = chantId
        }
    }

    fun increment() {
        val currentState = uiState.value
        if (currentState.sessionActive) {
            viewModelScope.launch {
                sessionRepository.updateProgress(currentState.currentDay, currentState.count + 1)
            }
        }
    }

    fun reset() {
        val currentState = uiState.value
        if (currentState.sessionActive) {
             viewModelScope.launch {
                sessionRepository.updateProgress(currentState.currentDay, 0)
            }
        }
    }
}
