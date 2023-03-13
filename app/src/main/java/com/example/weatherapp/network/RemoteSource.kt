package com.example.weatherapp.network

import android.content.ContentValues.TAG
import android.util.Log
import com.example.weatherapp.model.WeatherForecast
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RemoteSource : RemoteSourceInterface {
    companion object {
        private var instance: RemoteSource? = null
        fun getInstance(): RemoteSource {
            return instance ?: RemoteSource()
        }
    }


    override suspend fun getWeatherByLocation(lat: Double, long: Double, unit: String, lang: String): WeatherForecast {

        val apiService = RetroFitClient.getInstance().create(ApiInterface::class.java)
        Log.i(TAG, "called")
        val response = apiService.getTheWholeWeather(lat,long,unit,"minutely",lang,"992213628dbceb7e7fb06cf59035697d")
        Log.i(TAG, "called2 :${response.errorBody()}")
        if(response.isSuccessful){
            var result = response.body() as WeatherForecast
            Log.i(TAG, "DONE ${result.current.weather[0].description} response")
        }
        else{
            Log.i(TAG, "FAIL response${response.errorBody().toString()}")
        }
        return response.body() as WeatherForecast
    }

}

//992213628dbceb7e7fb06cf59035697d