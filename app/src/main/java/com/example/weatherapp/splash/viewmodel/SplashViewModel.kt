package com.example.weatherapp.splash.viewmodel

import androidx.lifecycle.ViewModel
import com.example.weatherapp.model.Repository
import com.example.weatherapp.utilities.CurrentUser
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject

class SplashViewModel  (var repo: Repository): ViewModel(){
    fun getLocatinSP(): LatLng? {
        if (repo.getSettingsSharedPreferences() != null) {
            CurrentUser.settings = repo.getSettingsSharedPreferences()!!
        }
        return  repo.getWeatherWithShP()
    }
}