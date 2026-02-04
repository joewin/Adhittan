package com.msis.adhittan

import android.app.Application
import com.msis.adhittan.data.repository.LibraryRepository
import com.msis.adhittan.data.repository.SessionRepository

class AdhittanApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}

class AppContainer(private val context: android.content.Context) {
    val sessionRepository by lazy {
        SessionRepository(context)
    }
    val libraryRepository by lazy {
        LibraryRepository(context)
    }
}
