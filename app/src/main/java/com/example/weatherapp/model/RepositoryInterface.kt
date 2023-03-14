package com.example.weatherapp.model

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow

interface RepositoryInterface {
    suspend fun getCurrentWeatherWithLocationInRepo(lat:Double, long:Double, lang :String,unit:String): WeatherForecast

    suspend fun storedLocations(): Flow<WeatherForecast>

    fun insertFavoriteWeather(weatherForecast: WeatherForecast)
    suspend fun searchWithLatLong(latLong :LatLng): Flow<WeatherForecast?>
    fun deleteFavoriteWeather(weatherForecast: WeatherForecast)
    fun addSettingsToSharedPreferences(setting:Settings)
    fun getSettingsSharedPreferences(): Settings?
    fun addWeatherToSHP(latLong: LatLng)
    fun getWeatherWithShP(): LatLng?
    fun insertAlert(alert: AlertData)
    fun deleteAlert(alert: AlertData)
    fun getAllAlertsInRepo(): LiveData<List<AlertData>>
}