package com.msis.adhittan.ui.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.msis.adhittan.AdhittanApplication

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            HomeViewModel(
                adhittanApplication().container.sessionRepository,
                adhittanApplication().container.libraryRepository
            )
        }
        initializer {
            LibraryViewModel(
                adhittanApplication().container.libraryRepository
            )
        }
        initializer {
            BeadViewModel(
                adhittanApplication().container.sessionRepository,
                adhittanApplication().container.libraryRepository
            )
        }
        initializer {
            SettingsViewModel(
                adhittanApplication().container.sessionRepository
            )
        }
        initializer {
            NawinViewModel(
                adhittanApplication().container.sessionRepository
            )
        }
    }
}

fun CreationExtras.adhittanApplication(): AdhittanApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AdhittanApplication)
