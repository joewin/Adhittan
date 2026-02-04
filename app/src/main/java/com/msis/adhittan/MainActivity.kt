package com.msis.adhittan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val container = (application as AdhittanApplication).container
        
        // Observe language changes
        kotlinx.coroutines.MainScope().launch {
            container.sessionRepository.language.collect { languageCode ->
                val localeList = android.os.LocaleList.forLanguageTags(languageCode)
                androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(
                    androidx.core.os.LocaleListCompat.forLanguageTags(languageCode)
                )
            }
        }

        setContent {
            AdhittanApp()
        }
    }
}
