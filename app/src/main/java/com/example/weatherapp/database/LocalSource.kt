package com.example.weatherapp.database
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.weatherapp.model.AlertData
import com.example.weatherapp.model.WeatherForecast
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.flow


class LocalSource(context: Context) {
        private var favDao: WeatherDAO
        private var alertDao :WeatherDAO


        init {
            val db = WeatherDatabase.getInstance(context.applicationContext)
            favDao = db.weatherDao()
            alertDao =db.weatherDao()

        }

        companion object{
            private var localSource:LocalSource? = null
            fun getInstance(context: Context):LocalSource{
                if(localSource == null){
                    localSource = LocalSource(context)
                }
                return localSource!!
            }
        }

        fun getAll() =  flow {
            emit(favDao.getAllStoredFav())
            Log.i(
                TAG,
                "getFavWeatherInRepoooooooo: ${favDao.getAllStoredFav().size} "
            )
        }

        fun insertWeather(weather: WeatherForecast) {
            favDao.insert (weather)
            Log.i(
                TAG,
                "inserttttFavWeatherInLocalll: ${weather.current.weather[0]} "
            )
        }

        fun deleteWeather(weather: WeatherForecast) {
            favDao.deleteFavWeather(weather)
        }
        fun search(latLong: LatLng) = flow {
            emit(  favDao.searchWithLatLong(latLong.latitude,latLong.longitude))
        }

         fun getAllStoredAlerts(): LiveData<List<AlertData>> {
        return alertDao.getAllStoredAlerts()
        }

//        fun getAllStoredAlerts() =  flow {
//        emit(alertDao.getAllStoredAlerts())
//            Log.i(TAG, "get all stored alerts in Localllllllllllllllllllllllllllllllllllllllllllllll ${alertDao.getAllStoredAlerts()}")
//        }

        fun inserAlert(alert: AlertData) {
            alertDao.insertAlert(alert)

            Log.i(TAG, "insert alerts in Localllllllllllllllllllllllllllllllllllllllllllllllllllllllll ${alert}")

        }

        fun deleteAlert(alert: AlertData) {
            alertDao.deleteAlert(alert)
        }
    }
