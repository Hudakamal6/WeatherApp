package com.example.weatherapp.network

import com.example.weatherapp.model.WeatherForecast

interface RemoteSourceInterface {

    suspend fun getWeatherByLocation(lat:Double,long:Double,unit:String,lang:String): WeatherForecast
}