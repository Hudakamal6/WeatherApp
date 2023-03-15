package com.example.weatherapp.favorite.viewModel

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.database.FavState
import com.example.weatherapp.model.Repository
import com.example.weatherapp.model.RepositoryInterface
import com.example.weatherapp.model.WeatherForecast
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch


class FavViewModel(var repo:RepositoryInterface): ViewModel() {

    var dbState = MutableStateFlow<FavState>(FavState.Loading)

    fun addtoFav(weather: WeatherForecast) {

        repo.insertFavoriteWeather(weather)
        Log.i(
            ContentValues.TAG,
            "insertttFavWeatherInViewwwwwwwmodellll: ${weather.current} "
        )
    }
    fun delete(weather:WeatherForecast) {
        repo.deleteFavoriteWeather(weather)
    }

    fun getAllFav(): MutableStateFlow<FavState> {
        var weatherList = mutableListOf<WeatherForecast>()
        viewModelScope.launch(Dispatchers.IO) {
            repo.storedLocations().catch {
                dbState.value = FavState.onFail(it)
            }.collect{
                weatherList.add(it)
                dbState.value = FavState.onSuccessList(weatherList)
            }
            Log.i(
                ContentValues.TAG,
                "insertttFavWeatherInViewwwwwwwmodellll: ${weatherList.size} "
            )
        }
        return dbState
    }
    fun searchResult(latLng: LatLng) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.searchWithLatLong(latLng).catch { dbState.value = FavState.onFail(it) }
                .collect{
                    dbState.value = FavState.onSuccessWeather(it as WeatherForecast)
                }
        }
    }
    suspend fun getWeather(lat: Double,long:Double): WeatherForecast{
        var weather:WeatherForecast? = null
        val job =viewModelScope.launch(Dispatchers.IO) {
            weather = repo.getFavWeather(lat,long,"en","metric")

            Log.i(
                ContentValues.TAG,
                "getFavWeatherInviewwwwwwmodellllll: ${weather!!.current.weather[0]} "
            )

        }
        job.join()
        return weather as WeatherForecast

    }

}
