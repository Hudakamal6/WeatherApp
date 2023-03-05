package com.example.weatherapp.network

import com.example.weatherapp.model.WeatherForecast
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("onecall")
    suspend fun getTheWholeWeather(@Query("lat") lat:Double,
                                   @Query("lon") long:Double,
                                   @Query("units") unit:String,
                                   @Query("exclude") exclude:String,
                                   @Query("lang") lang:String,
                                   @Query("appid")appid:String): Response<WeatherForecast>
}