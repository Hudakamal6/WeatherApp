package com.example.weatherapp.homescreen.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.*
import com.example.weatherapp.model.Repository
import com.example.weatherapp.model.Settings
import com.example.weatherapp.model.WeatherForecast
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(val _repo:Repository): ViewModel() {


    suspend fun getWeather(lat: Double, long: Double): WeatherForecast {
        var weather: WeatherForecast? = null
        val job = viewModelScope.launch(Dispatchers.IO) {
            weather = _repo.getCurrentWeatherWithLocationInRepo(lat, long, "metric")
        }
        job.join()

        Log.i(TAG, "getWeather: ${weather!!.hourly[0].weather}")

        return weather as WeatherForecast

    }


    fun getLocationSP(): LatLng? {
        var location = _repo.getWeatherWithShP()
        return location
    }

    fun getStoredSettings(): Settings? {
        return _repo.getSettingsSharedPreferences()
    }
}


//    private val _weatherFromNetwork = MutableLiveData<WeatherForecast>()
//    val weatherFromNetwork: LiveData<WeatherForecast> = _weatherFromNetwork
//
//    fun getWholeWeather(lat: Double, long: Double, unit: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            Log.i("TAG", "getWholeWeather: in view model ")
//            val response = _repo.getCurrentWeatherWithLocationInRepo(lat,long,unit)
//            _weatherFromNetwork.postValue(response)
//        }
//    }

//    var apiState = MutableStateFlow<RetroFitState>(RetroFitState.Loading)
//
//   private var currentWeather = WeatherForecast()
//    suspend fun getDataa(lat: Double, long: Double, unit: String) = viewModelScope.launch {
//        _repo.getCurrentWeather(lat,long,unit, lang = "").catch {
//            apiState.value = RetroFitState.onFail(Throwable("can not getting data"))
//        }.collect {
//            currentWeather = it
//            apiState.value = RetroFitState.onSuccess(currentWeather)
//        }
//    }
