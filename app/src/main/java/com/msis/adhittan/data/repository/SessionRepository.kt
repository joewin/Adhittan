package com.msis.adhittan.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.msis.adhittan.data.model.Session
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "adhittan_prefs")

class SessionRepository(private val context: Context) {

    private val KEY_ID = stringPreferencesKey("session_id")
    private val KEY_CHANT_ID = stringPreferencesKey("chant_id")
    private val KEY_START_DATE = longPreferencesKey("start_date")
    private val KEY_DURATION = intPreferencesKey("duration")
    private val KEY_TARGET = intPreferencesKey("target")
    private val KEY_CURRENT_DAY = intPreferencesKey("current_day")
    private val KEY_PROGRESS_MAP = stringPreferencesKey("progress_map") // "day:count,day:count"
    private val KEY_IS_ACTIVE = booleanPreferencesKey("is_active")
    private val KEY_LANGUAGE = stringPreferencesKey("app_language")
    
    // Nawin Persistence
    private val KEY_BIRTH_DAY = intPreferencesKey("nawin_birth_day") // 1-7
    private val KEY_NAWIN_INDEX = intPreferencesKey("nawin_current_index") // 0-8
    private val KEY_IS_NAWIN_ACTIVE = booleanPreferencesKey("is_nawin_active")


    val language: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_LANGUAGE] ?: "en"
    }

    suspend fun setLanguage(code: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_LANGUAGE] = code
        }
    }

    val progressMap: Flow<Map<Int, Int>> = context.dataStore.data.map { prefs ->
        val progressString = prefs[KEY_PROGRESS_MAP] ?: ""
        if (progressString.isNotEmpty()) {
            progressString.split(",").associate {
                val parts = it.split(":")
                (parts.getOrNull(0)?.toIntOrNull() ?: 0) to (parts.getOrNull(1)?.toIntOrNull() ?: 0)
            }
        } else {
            emptyMap()
        }
    }


    val activeSession: Flow<Session?> = context.dataStore.data.map { prefs ->
        val isActive = prefs[KEY_IS_ACTIVE] ?: false
        if (!isActive) {
            null
        } else {
            val progressString = prefs[KEY_PROGRESS_MAP] ?: ""
            val progressMap = if (progressString.isNotEmpty()) {
                progressString.split(",").associate {
                    val parts = it.split(":")
                    (parts.getOrNull(0)?.toIntOrNull() ?: 0) to (parts.getOrNull(1)?.toIntOrNull() ?: 0)
                }
            } else {
                emptyMap()
            }

            Session(
                id = prefs[KEY_ID] ?: "",
                chantId = prefs[KEY_CHANT_ID] ?: "",
                startDate = prefs[KEY_START_DATE] ?: 0L,
                durationDays = prefs[KEY_DURATION] ?: 0,
                targetCountPerDay = prefs[KEY_TARGET] ?: 0,
                currentDay = prefs[KEY_CURRENT_DAY] ?: 1,
                dailyProgress = progressMap,
                isActive = true
            )
        }
    }

    suspend fun saveSession(session: Session) {
        val progressString = session.dailyProgress.entries.joinToString(",") { "${it.key}:${it.value}" }
        
        context.dataStore.edit { prefs ->
            prefs[KEY_ID] = session.id
            prefs[KEY_CHANT_ID] = session.chantId
            prefs[KEY_START_DATE] = session.startDate
            prefs[KEY_DURATION] = session.durationDays
            prefs[KEY_TARGET] = session.targetCountPerDay
            prefs[KEY_CURRENT_DAY] = session.currentDay
            prefs[KEY_PROGRESS_MAP] = progressString
            prefs[KEY_IS_ACTIVE] = session.isActive
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs[KEY_IS_ACTIVE] = false
        }
    }
    
    private val KEY_VIBRATION = booleanPreferencesKey("vibration_enabled")
    
    val vibrationEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_VIBRATION] ?: true
    }

    suspend fun setVibration(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_VIBRATION] = enabled
        }
    }

    suspend fun updateProgress(currentDay: Int, count: Int) {
         context.dataStore.edit { prefs ->
             // Must read current map to update safely, but DataStore allows transform
             // Simpler approach: Read current value in edit block
             val currentString = prefs[KEY_PROGRESS_MAP] ?: ""
             val currentMap = if (currentString.isNotEmpty()) {
                currentString.split(",").associate {
                    val parts = it.split(":")
                    (parts.getOrNull(0)?.toIntOrNull() ?: 0) to (parts.getOrNull(1)?.toIntOrNull() ?: 0)
                }.toMutableMap()
            } else {
                mutableMapOf()
            }
            
            currentMap[currentDay] = count
            prefs[KEY_PROGRESS_MAP] = currentMap.entries.joinToString(",") { "${it.key}:${it.value}" }
         }
    }

    // Nawin State Flows
    val nawinBirthDay: Flow<Int?> = context.dataStore.data.map { prefs ->
        prefs[KEY_BIRTH_DAY]
    }

    val nawinIndex: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[KEY_NAWIN_INDEX] ?: 0
    }

    val isNawinActive: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_IS_NAWIN_ACTIVE] ?: false
    }

    suspend fun startNawin(birthDayValue: Int) {
        context.dataStore.edit { prefs ->
            prefs[KEY_BIRTH_DAY] = birthDayValue
            prefs[KEY_NAWIN_INDEX] = 0
            prefs[KEY_IS_NAWIN_ACTIVE] = true
        }
    }

    suspend fun updateNawinIndex(index: Int) {
        context.dataStore.edit { prefs ->
            prefs[KEY_NAWIN_INDEX] = index
        }
    }

    suspend fun clearNawin() {
        context.dataStore.edit { prefs ->
            prefs[KEY_IS_NAWIN_ACTIVE] = false
        }
    }
}

