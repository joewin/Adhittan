package com.msis.adhittan.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msis.adhittan.data.repository.SessionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val sessionRepository: SessionRepository
) : ViewModel() {

    val vibrationEnabled: StateFlow<Boolean> = sessionRepository.vibrationEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    fun setVibration(enabled: Boolean) {
        viewModelScope.launch {
            sessionRepository.setVibration(enabled)
        }
    }

    val language: StateFlow<String> = sessionRepository.language
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "en"
        )

    fun setLanguage(code: String) {
        viewModelScope.launch {
            sessionRepository.setLanguage(code)
        }
    }
}
