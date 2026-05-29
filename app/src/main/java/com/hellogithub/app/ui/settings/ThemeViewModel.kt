package com.hellogithub.app.ui.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore("settings")

enum class ThemeMode { SYSTEM, LIGHT, DARK }

class ThemeViewModel(private val context: Context) : ViewModel() {

    private val THEME_KEY = stringPreferencesKey("theme_mode")

    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    init {
        viewModelScope.launch {
            context.dataStore.data.map { prefs ->
                when (prefs[THEME_KEY]) {
                    "light" -> ThemeMode.LIGHT
                    "dark" -> ThemeMode.DARK
                    else -> ThemeMode.SYSTEM
                }
            }.collect { mode ->
                _themeMode.value = mode
            }
        }
    }

    fun setTheme(mode: ThemeMode) {
        _themeMode.value = mode
        viewModelScope.launch {
            context.dataStore.edit { prefs ->
                prefs[THEME_KEY] = when (mode) {
                    ThemeMode.SYSTEM -> "system"
                    ThemeMode.LIGHT -> "light"
                    ThemeMode.DARK -> "dark"
                }
            }
        }
    }
}
