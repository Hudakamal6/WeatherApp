package com.example.weatherapp.network

import com.example.weatherapp.model.WeatherForecast

sealed class RetroFitState(){
    class onSuccess(val currentWeather: WeatherForecast ):RetroFitState()
    class onFail(val msg: Throwable ):RetroFitState()
    object Loading : RetroFitState()
}
