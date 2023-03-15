package com.example.weatherapp.model

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

@Entity(primaryKeys = arrayOf("lat", "lon"), tableName = "weathers")
data class WeatherForecast(
                           var lat:Double,
                           @NonNull
                           var lon:Double,
                           var timezone:String,
                           var current:CurrentWeather,
                           var hourly:List<HourlyWeather>,
                           var daily:List<DailyWeather>,
                           val alerts: List<Alert>?): Serializable

data class CurrentWeather(var dt:Int,
                          var temp:Double,
                          var pressure:Int,
                          var humidity:Int,
                          var clouds:Int,
                          var wind_speed:Double,
                          var weather:List<Weather>)

data class HourlyWeather(var dt:Int,
                         var temp:Double,
                         var wind_speed:Double,
                         var weather:List<Weather>)

data class DailyWeather(var dt:Int,
                        var temp:Temprature,
                        var weather:List<Weather>)

data class Weather(var id:Int,
                   var main:String,
                   var description:String,
                   var icon:String)

data class Temprature(var day:Double,
                      var min:Double,
                      var max:Double,
                      var night:Double,
                      var eve:Double,
                      var morn:Double)

data class Alert (var senderName: String,
                  var event: String,
                  var start: Long,
                  var end: Long,
                  var lat: Double,
                  var long: Double,
                  var description: String,
                  var tags: List<String>)

data class Settings(var language:Boolean =  true,
                    var unit:Int =0 ,
                    var location:Int = 1,
                    var notification:Boolean = true)

@Entity(primaryKeys = arrayOf("lat", "lon"), tableName = "addresses")
data class WeatherAddress(var address:String,
                          @NonNull
                          var lat:Double,
                          @NonNull
                          var lon:Double)

@Entity(tableName = "AlertsTable", primaryKeys = ["idHashLongFromLonLatStartStringEndStringAlertType"])
data class AlertData(
    val address: String,
    val longitudeString: String,
    val latitudeString: String,
    val startString: String,
    val endString: String,
    val startDT: Int,
    val endDT: Int,
    val idHashLongFromLonLatStartStringEndStringAlertType: Long,
    val alertType: String)