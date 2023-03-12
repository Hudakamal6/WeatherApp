package com.example.weatherapp.favorite.view

import com.example.weatherapp.model.WeatherForecast

interface OnFavWeatherClickListener {
    fun onRemoveBtnClick(weather: WeatherForecast)
    fun onFavItemClick(weather: WeatherForecast)
}