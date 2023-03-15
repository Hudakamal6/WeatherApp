package com.example.weatherapp.network

import com.example.weatherapp.model.WeatherForecast

sealed class RetroFitState(){
    data class onSuccess(val weatherForecast: WeatherForecast):RetroFitState()
    data class onFail(val msg: Throwable ):RetroFitState()
    data class Loading(val m:String) : RetroFitState()
}
