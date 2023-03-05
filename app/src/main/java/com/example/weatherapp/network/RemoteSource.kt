package com.example.weatherapp.network

import android.util.Log
import com.example.weatherapp.model.WeatherForecast

class RemoteSource : RemoteSourceInterface {
    override suspend fun getCurrentWeatherWithLocation(lat:Double,long:Double,unit:String,lang:String): WeatherForecast {
        //var result:WeatherForecast
        val apiService = RetroFitClient.getInstance().create(ApiInterface::class.java)
        Log.i("TAG", "getCurrentWeatherWithLocation: in Remooooote source apiService")

        val response = apiService.getTheWholeWeather(lat,long,unit,"minutely",lang,"375d11598481406538e244d548560243")
        Log.i("TAG", "getCurrentWeatherWithLocation: in Remooooote source response")
        if(response.isSuccessful){
            var result = response.body() as WeatherForecast
            Log.i("TAG", "successsssssssssssssssssssssssss ${result.current.weather[0].description} response")
        }
        else{
            Log.i("TAG", "faillllllllllllllllllllllllllled response")
        }
        return response.body() as WeatherForecast
    }

    companion object{
        private var instance: RemoteSource? = null
        fun getInstance(): RemoteSource{
            return instance?: RemoteSource()
        }
    }
}