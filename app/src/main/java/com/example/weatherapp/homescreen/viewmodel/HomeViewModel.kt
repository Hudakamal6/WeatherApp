package com.example.weatherapp.homescreen.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.weatherapp.model.Repository
import com.example.weatherapp.model.RepositoryInterface
import com.example.weatherapp.model.WeatherForecast
import com.example.weatherapp.network.RetroFitState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(val _repo:RepositoryInterface): ViewModel() {

    private val _weatherFromNetwork = MutableLiveData<WeatherForecast>()
    val weatherFromNetwork: LiveData<WeatherForecast> = _weatherFromNetwork

    fun getWholeWeather(lat: Double, long: Double, unit: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.i("TAG", "getWholeWeather: in view model ")
            val response = _repo.getCurrentWeatherWithLocationInRepo(lat,long,unit)
            _weatherFromNetwork.postValue(response)
        }
    }

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
}