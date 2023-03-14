package com.example.weatherapp.settingsscreen.viewmodel

import androidx.lifecycle.ViewModel
import com.example.weatherapp.model.Repository
import com.example.weatherapp.model.Settings

class SettingsViewModel( val repo:Repository) : ViewModel() {
    fun setSettingsSharedPrefs(settings: Settings){
        repo.addSettingsToSharedPreferences(settings)
    }
    fun getStoredSettings(): Settings?{
        return repo.getSettingsSharedPreferences()
    }

}