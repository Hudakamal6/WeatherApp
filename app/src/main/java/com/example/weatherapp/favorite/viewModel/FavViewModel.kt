package com.example.weatherapp.favorite.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.database.LocalState
import com.example.weatherapp.model.Repository
import com.example.weatherapp.model.WeatherForecast
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch


class FavViewModel(var repo:Repository): ViewModel() {

    var dbState = MutableStateFlow<LocalState>(LocalState.Loading)
    fun addtoFav(weather: WeatherForecast) {

        repo.insertFavoriteWeather(weather)
    }
    fun delete(weather:WeatherForecast) {
        repo.deleteFavoriteWeather(weather)
    }

    fun getAllFav(): MutableStateFlow<LocalState> {
        var weatherList = mutableListOf<WeatherForecast>()
        viewModelScope.launch(Dispatchers.IO) {
            repo.storedLocations().catch {
                dbState.value = LocalState.onFail(it)
            }.collect{
                weatherList.add(it)
                dbState.value = LocalState.onSuccessList(weatherList)
            }
        }
        return dbState
    }
    fun searchResult(latLng: LatLng) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.searchWithLatLong(latLng).catch { dbState.value = LocalState.onFail(it) }
                .collect{
                    dbState.value = LocalState.onSuccessWeather(it as WeatherForecast)
                }
        }
    }
    suspend fun getWeather(lat: Double,long:Double): WeatherForecast{
        var weather:WeatherForecast? = null
        val job =viewModelScope.launch(Dispatchers.IO) {
            weather = repo.getCurrentWeatherWithLocationInRepo(lat,long,"metric")

        }
        job.join()
        return weather as WeatherForecast

    }

}
