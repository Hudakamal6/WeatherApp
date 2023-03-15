package com.example.weatherapp.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeRepository : RepositoryInterface {

    private  var favList :MutableList<WeatherForecast> = mutableListOf<WeatherForecast>()

    private var  alertList = mutableListOf<AlertData>()
    override suspend fun getFavWeather(
        lat: Double,
        long: Double,
        lang: String,
        unit: String
    ): WeatherForecast {
        TODO("Not yet implemented")
    }

    private val observableAlertList = MutableLiveData<List<AlertData>>(alertList)

    override suspend fun getCurrentWeatherWithLocationInRepo(
        lat: Double,
        long: Double,
        lang: String,
        unit: String
    ): Flow<WeatherForecast> {
        TODO("Not yet implemented")
    }

    override suspend fun storedLocations(): Flow<WeatherForecast> = flow {
      for(weather in favList){
          emit(weather)
      }
    }
    override fun insertFavoriteWeather(weatherForecast: WeatherForecast) {
       favList.add(weatherForecast)
    }
    override fun deleteFavoriteWeather(weatherForecast: WeatherForecast) {
        favList.remove(weatherForecast)
    }

    override fun insertAlert(alert: AlertData) {
        alertList.add(alert)
        refreshLiveData()
    }

    override fun deleteAlert(alert: AlertData) {
        alertList.remove(alert)
        refreshLiveData()
    }

    override fun getAllAlertsInRepo(): LiveData<List<AlertData>> {
        return observableAlertList
    }
    fun refreshLiveData(){
        observableAlertList.postValue(alertList)
    }

    override fun addSettingsToSharedPreferences(setting: Settings) {
        TODO("Not yet implemented")
    }

    override fun getSettingsSharedPreferences(): Settings? {
        TODO("Not yet implemented")
    }

    override fun addWeatherToSHP(latLong: LatLng) {
        TODO("Not yet implemented")
    }

    override fun getWeatherWithShP(): LatLng? {
        TODO("Not yet implemented")
    }
    override suspend fun searchWithLatLong(latLong: LatLng): Flow<WeatherForecast?> {
        TODO("Not yet implemented")
    }
}