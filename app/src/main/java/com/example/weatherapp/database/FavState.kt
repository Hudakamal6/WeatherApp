package com.example.weatherapp.database

import com.example.weatherapp.model.WeatherForecast

sealed class FavState{
    class onSuccessList(val weatherList : List<WeatherForecast> ):FavState()
    class onSuccessWeather(val weather: WeatherForecast):FavState()
    class onFail(val msg: Throwable ):FavState()
    object Loading : FavState()
}
