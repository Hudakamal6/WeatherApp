package com.example.weatherapp.homescreen.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.*
import com.example.weatherapp.database.FavState
import com.example.weatherapp.model.Repository
import com.example.weatherapp.model.RepositoryInterface
import com.example.weatherapp.model.Settings
import com.example.weatherapp.model.WeatherForecast
import com.example.weatherapp.network.RetroFitState
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(val _repo:RepositoryInterface): ViewModel() {

    var apiState = MutableStateFlow<RetroFitState>(RetroFitState.Loading(""))

//    suspend fun getWeather(lat: Double, long: Double,lang:String, unit:String): WeatherForecast {
//        var weather: WeatherForecast? = null
//        val job = viewModelScope.launch(Dispatchers.IO) {
//            weather = _repo.getCurrentWeatherWithLocationInRepo(lat, long,lang,unit )
//        }
//        job.join()
//
//        Log.i(TAG, "getWeather: ${weather!!.hourly[0].weather}")
//
//        return weather as WeatherForecast
//
//    }
   suspend fun getWeather(lat: Double, long: Double,lang:String, unit:String): MutableStateFlow<RetroFitState> {
    var weather: WeatherForecast
    viewModelScope.launch(Dispatchers.IO) {
        _repo.getCurrentWeatherWithLocationInRepo(lat, long, lang, unit)
            .catch {
                apiState.value = RetroFitState.onFail(it)
            }.collect {
                weather = it
                apiState.value = RetroFitState.onSuccess(weather)
            }
    }
    return apiState
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
