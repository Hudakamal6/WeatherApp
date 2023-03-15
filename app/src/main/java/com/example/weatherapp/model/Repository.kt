package com.example.weatherapp.model

import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.weatherapp.database.LocalSource
import com.example.weatherapp.network.RemoteSource
import com.example.weatherapp.network.RemoteSourceInterface
import com.example.weatherapp.utilities.Constants
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import kotlinx.coroutines.flow.flow


class Repository (var remoteSource: RemoteSourceInterface,
                  var localSource: LocalSource,
                  var context: Context,
                  var sharedPreferences:SharedPreferences) :RepositoryInterface{

    var storedWeathers: List<WeatherForecast>? = null
    var searchWeather :WeatherForecast? = null
    //private lateinit var sharedPreferences: SharedPreferences


    companion object{
        private var instance: Repository? = null
        fun getInstance(remoteSource: RemoteSource,
                        localSource: LocalSource,
                        context: Context,
                        appSHP:SharedPreferences):Repository{
            return instance?: Repository(remoteSource,localSource,
                context,appSHP)
        }
    }

    //Retrofit

    override suspend fun getCurrentWeatherWithLocationInRepo(lat:Double, long:Double, lang :String,unit:String ) =
        flow {
            val weatherinrepo: WeatherForecast
            weatherinrepo = remoteSource.getWeatherByLocation(lat,long,unit,Constants.languages.en.langValue)
            emit(weatherinrepo)
            Log.i(
                TAG,
                "getCurrentWeatherWithLocationInRepooooo: ${weatherinrepo.current.weather[0].description} ")

        }

    override suspend fun getFavWeather(lat:Double, long:Double, lang :String,unit:String ): WeatherForecast {

        val weatherinrepo =
            remoteSource.getWeatherByLocation(lat, long, unit, Constants.languages.en.langValue)
        Log.i(
            TAG,
            "getFavWeatherInRepoooooooo: ${weatherinrepo.current.weather[0].description} "
        )
        return weatherinrepo

    }

    override suspend fun storedLocations() = flow {

        var updatedWeather: WeatherForecast
        localSource.getAll()
            .collect{
                storedWeathers = it
                Log.i(
                    TAG,
                    "getFavWeatherInRepoooooooo: ${it.size} "
                )

            }
        if (storedWeathers != null) {
            for (weather in storedWeathers!!) {
                updatedWeather = getFavWeather(weather.lat,weather.lon,"en","metric")
                emit(updatedWeather)
            }

        }
    }
   override fun insertFavoriteWeather(weather: WeatherForecast) {
        localSource.insertWeather(weather)
       Log.i(
           TAG,
           "insertFavWeatherInRepoooooooo: ${weather.current.weather[0]} "
       )

    }

    override fun deleteFavoriteWeather(weather: WeatherForecast) {
        localSource.deleteWeather(weather)
    }

    override suspend fun searchWithLatLong (latLong: LatLng) = flow {
        localSource.search(latLong).collect{
            searchWeather = it
        }
        if(searchWeather !=null) {
            emit(searchWeather)
        }
    }


    // settings Shared Prefrences

   override fun addSettingsToSharedPreferences(setting:Settings) {
        var prefEditor = sharedPreferences.edit()
        var gson= Gson()
        var settingStr = gson.toJson(setting)
        prefEditor.putString(Constants.MY_SETTINGS_PREFS,settingStr)
        prefEditor.commit()
        Log.i(TAG, "addSettingsToSharedPreferences: Done")
    }

   override fun getSettingsSharedPreferences(): Settings? {
        var settingStr = sharedPreferences.getString(Constants.MY_SETTINGS_PREFS,"")
        var gson= Gson()
        var settingObj:Settings? = gson.fromJson(settingStr,Settings::class.java)
        return settingObj
    }


    // weather shared prefrence

    override fun addWeatherToSHP(latLong: LatLng) {
        var prefEditor = sharedPreferences.edit()
        var gson= Gson()
        var weatherStr = gson.toJson(latLong)
        prefEditor.putString(Constants.MY_CURRENT_LOCATION,weatherStr)
        prefEditor.commit()
    }

    override fun getWeatherWithShP(): LatLng? {
        var latLong = sharedPreferences.getString(Constants.MY_CURRENT_LOCATION,"")
        var gson= Gson()
        var location:LatLng? = gson.fromJson(latLong,LatLng::class.java)
        return location
    }

    // alerts

  override  fun getAllAlertsInRepo(): LiveData<List<AlertData>> {
        return localSource.getAllStoredAlerts()
    }
    override fun insertAlert(alert: AlertData) {
        localSource.inserAlert(alert)
        Log.i(TAG, "Insertt  alerts in repoooooooooooooooooooooooooooooooooooooooooooooo ${alert} ")
    }

    override fun deleteAlert(alert: AlertData) {
        localSource.deleteAlert(alert)
    }

//    fun storedAlerts() = flow {
//        var storedAlerts: List<AlertData>? = null
//        localSource.getAllStoredAlerts()
//            .collect {
//                storedAlerts = it
//            }
//        emit(storedAlerts)
//        Log.i(TAG, "get all stored alerts in repoooooooooooooooooooooooooooooooooooooooooooooo ${storedAlerts?.size}")
//        }

}

