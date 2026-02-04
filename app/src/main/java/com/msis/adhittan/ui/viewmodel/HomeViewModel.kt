package com.msis.adhittan.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msis.adhittan.data.model.Session
import com.msis.adhittan.data.repository.LibraryRepository
import com.msis.adhittan.data.repository.SessionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val sessionRepository: SessionRepository,
    private val libraryRepository: LibraryRepository
) : ViewModel() {

    val activeSession: StateFlow<Session?> = sessionRepository.activeSession
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun startNewSession(chantId: String, days: Int, goal: Int) {
        viewModelScope.launch {
            val newSession = Session(
                id = System.currentTimeMillis().toString(),
                chantId = chantId,
                startDate = System.currentTimeMillis(),
                durationDays = days,
                targetCountPerDay = goal,
                currentDay = 1,
                dailyProgress = mapOf(1 to 0),
                isActive = true
            )
            sessionRepository.saveSession(newSession)
            sessionRepository.clearNawin() // Ensure Nawin is off
        }
    }

    fun startNawinSession() {
        viewModelScope.launch {
            sessionRepository.clearSession() // Ensure standard is off
            sessionRepository.startNawin(1) // Value 1 is a dummy now as we always use Column 1
            // Initialize progress for Nawin day 0
            sessionRepository.updateProgress(0, 0)
        }
    }

    val isNawinActive: StateFlow<Boolean> = sessionRepository.isNawinActive
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun finishSession() {
        viewModelScope.launch {
            sessionRepository.clearSession()
            sessionRepository.clearNawin()
        }
    }
}

