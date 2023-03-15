package com.example.weatherapp.splash.viewmodel

import androidx.lifecycle.ViewModel
import com.example.weatherapp.model.Repository
import com.example.weatherapp.utilities.UserHelper
import com.google.android.gms.maps.model.LatLng

class SplashViewModel  (var repo: Repository): ViewModel(){
    fun getLocatinSP(): LatLng? {
        if (repo.getSettingsSharedPreferences() != null) {
            UserHelper.settings = repo.getSettingsSharedPreferences()!!
        }
        return  repo.getWeatherWithShP()
    }
}