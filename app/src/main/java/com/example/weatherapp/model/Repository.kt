package com.example.weatherapp.model

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.safyweather.db.LocalSourceInterface
import com.example.weatherapp.network.ApiInterface
import com.example.weatherapp.network.RemoteSourceInterface
import com.example.weatherapp.network.RetroFitClient
import com.example.weatherapp.utilities.MY_CURRENT_WEATHER_OBJ
import com.example.weatherapp.utilities.MY_SETTINGS_PREFS
import com.example.weatherapp.utilities.arrayOfLangs
import com.google.gson.Gson


class Repository (var remoteSource: RemoteSourceInterface,
                  var localSource: LocalSourceInterface,
                  var context: Context,
                  var appSHP:SharedPreferences): RepositoryInterface{
    lateinit var currentWeather: WeatherForecast

    companion object {
        private var instance: Repository? = null
        fun getInstance(remoteSource: RemoteSourceInterface,
                        localSource: LocalSourceInterface,
                        context: Context,
                        appSHP: SharedPreferences): Repository {
            return instance ?: Repository(remoteSource,localSource,context,appSHP)
        }
    }

    override suspend fun getCurrentWeatherWithLocationInRepo(lat:Double,long:Double,unit:String): WeatherForecast {
        Log.i("TAG", "getCurrentWeatherWithLocationInRepoooooooooooooo: ")

        if(getSettingsSharedPreferences()?.language as Boolean){

            var weatherinrepo = remoteSource.getCurrentWeatherWithLocation(lat,long,unit,arrayOfLangs[0])
            Log.i("TAG", "getCurrentWeatherWithLocationInRepo: ${weatherinrepo.current.weather[0].description} ")
            return weatherinrepo
        }

        var weatherinrepo2 = remoteSource.getCurrentWeatherWithLocation(lat,long,unit,arrayOfLangs[1])
        Log.i("TAG", "getCurrentWeatherWithLocationInRepo: ${weatherinrepo2.current.weather[0].description} ")
        return weatherinrepo2
    }

    override val storedAddresses: LiveData<List<WeatherAddress>>
        get() = localSource.getAllAddresses()

    override fun getAllWeathersInRepo(): LiveData<List<WeatherForecast>> {
        return localSource.getAllStoredWeathers()
    }

    override fun getOneWeather(lat: Double, long: Double): LiveData<WeatherForecast> {
        return localSource.getWeatherWithLatLong(lat,long)
    }

    override fun insertFavoriteAddress(address: WeatherAddress) {
        localSource.insertFavoriteAddress(address)
    }

    override fun deleteFavoriteAddress(address: WeatherAddress) {
        localSource.deleteFavoriteAddress(address)
    }

    override fun insertWeather(weather: WeatherForecast) {
        localSource.insertWeather(weather)
    }

    override fun deleteWeather(weather: WeatherForecast) {
        localSource.deleteWeather(weather)
    }

    override fun addSettingsToSharedPreferences(settings: Settings) {
        var prefEditor = appSHP.edit()
        var gson=Gson()
        var settingStr = gson.toJson(settings)
        prefEditor.putString(MY_SETTINGS_PREFS,settingStr)
        prefEditor.commit()
    }

    override fun getSettingsSharedPreferences(): Settings? {
        var settingStr = appSHP.getString(MY_SETTINGS_PREFS,"")
        var gson=Gson()
        var settingsObj:Settings? = gson.fromJson(settingStr,Settings::class.java)
        return settingsObj
    }

    override fun addWeatherToSharedPreferences(weather: WeatherForecast) {
        var prefEditor = appSHP.edit()
        var gson=Gson()
        var weatherStr = gson.toJson(weather)
        prefEditor.putString(MY_CURRENT_WEATHER_OBJ,weatherStr)
        prefEditor.commit()
    }

    override fun getWeatherSharedPreferences(): WeatherForecast? {
        var weatherStr = appSHP.getString(MY_CURRENT_WEATHER_OBJ,"")
        var gson=Gson()
        var weatherObj:WeatherForecast? = gson.fromJson(weatherStr,WeatherForecast::class.java)
        return weatherObj
    }

    override fun getAllAlertsInRepo(): LiveData<List<AlertData>> {
        return localSource.getAllStoredAlerts()
    }

    override fun insertAlertInRepo(alert: AlertData) {
        localSource.insertAlert(alert)
    }

    override fun deleteAlertInRepo(alert: AlertData) {
        localSource.deleteAlert(alert)
    }
}

//
////    fun getCurrentWeather(lat: Double, long: Double, unit: String, lang: String) = flow {
////        val apiService = RetroFitClient.getInstance().create(ApiInterface::class.java)
////        Log.i("TAG", "getCurrentWeatherWithLocation: in Remooooote source apiService")
////        val response = apiService.getTheWholeWeather(lat, long,unit, "minutely", lang, "992213628dbceb7e7fb06cf59035697d")
////
////        currentWeather = response.body() as WeatherForecast
////
////        emit(currentWeather)
////    }
//
//
