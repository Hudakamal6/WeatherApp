package com.example.weatherapp.utilities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import androidx.room.TypeConverter
import com.example.weatherapp.model.*
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*


// covertor class for date and time

object UserHelper {
    var location = LatLng(30.033333,31.233334)
    // var location = LatLng(55.751244,37.618423)
    var settings = Settings()
    var alertLoc = location
    var networkState = true
}
class Convertors {

    companion object {
        fun getDateFromInt(day: Int, month: Int, year: Int): String {
            var commingDate: Date = Date(year, month, day)
            var dateFormat: SimpleDateFormat = SimpleDateFormat("dd MMM")
            var formatedDate: String = dateFormat.format(commingDate)

            return formatedDate
        }

        fun getDateFormat(dtInTimeStamp: Int): String {

            var currentDate: Date = Date(dtInTimeStamp.toLong() * 1000)
            var dateFormat: SimpleDateFormat = SimpleDateFormat("EEE MMM d")
            var formatedDate: String = dateFormat.format(currentDate)

            return formatedDate

        }

        fun getDayFormat(dtInTimeStamp: Int): String {

            var currentDate: Date = Date(dtInTimeStamp.toLong() * 1000)
            var dateFormat: SimpleDateFormat = SimpleDateFormat("EEE")
            var formatedDate: String = dateFormat.format(currentDate)

            return formatedDate

        }

        fun getTimeFormat(dtInTimeStamp: Int): String {

            var currentTime: Date = Date(dtInTimeStamp.toLong() * 1000)
            var timeFormat: SimpleDateFormat = SimpleDateFormat("HH:mm")
            var formatedTime: String = timeFormat.format(currentTime)

            return formatedTime

        }
        fun getDateFormat(txt: String): Date? {
            val formatter = SimpleDateFormat("d MMMM, yyyy HH:mm")
            formatter.timeZone = TimeZone.getTimeZone("GMT+2")
            val date = formatter.parse(txt)
            return date
        }
    }
}

fun Context.vectorToBitmap(drawableId: Int): Bitmap? {
    val drawable = ContextCompat.getDrawable(this, drawableId) ?: return null
    val bitmap = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    ) ?: return null
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}



// convertor class for weather

class WeatherConverter {
    var gson = Gson()

    @TypeConverter
    fun currentWeatherToString(currentWeather: CurrentWeather):String{
        return gson.toJson(currentWeather)
    }
    @TypeConverter
    fun stringToCurrentWeather(currentWeatherString: String): CurrentWeather {
        return gson.fromJson(currentWeatherString, CurrentWeather::class.java)
    }

    @TypeConverter
    fun weatherToString(weather: Weather):String{
        return gson.toJson(weather)
    }
    @TypeConverter
    fun stringToWeather(weatherString:String): Weather {
        return gson.fromJson(weatherString, Weather::class.java)
    }

    @TypeConverter
    fun weatherListToString(weatherList: List<Weather>):String{
        return gson.toJson(weatherList)
    }
    @TypeConverter
    fun stringToWeatherList(weatherListString:String):List<Weather>{
        var list = object : TypeToken<List<Weather>>(){}.type
        return gson.fromJson(weatherListString, list)
    }

    @TypeConverter
    fun hourlyWeatherToString(hourlyWeather: HourlyWeather):String{
        return gson.toJson(hourlyWeather)
    }
    @TypeConverter
    fun stringToHourlyWeather(hourlyWeatherString:String): HourlyWeather {
        return gson.fromJson(hourlyWeatherString, HourlyWeather::class.java)
    }

    @TypeConverter
    fun hourlyListToString(hourlyList:List<HourlyWeather>):String{
        return gson.toJson(hourlyList)
    }
    @TypeConverter
    fun stringToHourlyList(hourlyListString:String):List<HourlyWeather>{
        var list = object : TypeToken<List<HourlyWeather>>(){}.type
        return gson.fromJson(hourlyListString,list)
    }


    @TypeConverter
    fun dailyWeatherToString(dailyWeather: DailyWeather):String{
        return gson.toJson(dailyWeather)
    }
    @TypeConverter
    fun stringToDailyWeather(dailyWeatherString:String): DailyWeather {
        return gson.fromJson(dailyWeatherString, DailyWeather::class.java)
    }
    @TypeConverter
    fun dailyListToString(dailyList:List<DailyWeather>):String{
        return gson.toJson(dailyList)
    }
    @TypeConverter
    fun stringToDailyList(dailyListString:String):List<DailyWeather>{
        var list = object : TypeToken<List<DailyWeather>>(){}.type
        return gson.fromJson(dailyListString,list)
    }

    @TypeConverter
    fun tempratureToString(temprature: Temprature):String{
        return gson.toJson(temprature)
    }
    @TypeConverter
    fun stringToTemprature(tempratureString:String): Temprature {
        return gson.fromJson(tempratureString, Temprature::class.java)
    }

    //alert list
    /*@TypeConverter
    fun alertListToString(allAlerts:List<Alert>):String{
        return gson.toJson(allAlerts)
    }

    @TypeConverter
    fun stringToAlertsList(allAlertString:String):List<Alert>{
        var list = object :TypeToken<List<Alert>>(){}.type
        return gson.fromJson(allAlertString,list)
    }

    //alert
    @TypeConverter
    fun alertToString(oneAlert:Alert):String{
        return gson.toJson(oneAlert)
    }

    @TypeConverter
    fun stringToAlert(oneAlertString:String):Alert{
        var list = object :TypeToken<Alert>(){}.type
        return gson.fromJson(oneAlertString,list)
    }*/

    //tag
    @TypeConverter
    fun tagToString(oneTag:List<String>):String{
        return gson.toJson(oneTag)
    }

    @TypeConverter
    fun stringToTags(oneTagString:String):List<String>{
        var list = object : TypeToken<List<String>>(){}.type
        return gson.fromJson(oneTagString,list)
    }

    //covert from / to alerts
    @TypeConverter
    fun fromAlertToString(alertList: List<Alert>?) : String{
        return gson.toJson(alertList)
    }

    @TypeConverter
    fun fromStringToAlert(alertListString: String) : List<Alert>?{
        if(alertListString == null){
            return Collections.emptyList()
        }else{
            var list = object : TypeToken<List<Alert?>?>(){}.type
            return gson.fromJson(alertListString, list)
        }
    }

    @TypeConverter
    fun fromAlertDataToString(alertList: List<AlertData>) : String{
        return gson.toJson(alertList)
    }

    @TypeConverter
    fun fromStringToAlertData(alertListString: String) : List<AlertData> {
        var list = object : TypeToken<List<AlertData>>(){}.type
        return gson.fromJson(alertListString, list)
    }

    @TypeConverter
    fun fromOneAlertDataToString(alert: AlertData) : String{
        return gson.toJson(alert)
    }

    @TypeConverter
    fun fromStringToOneAlertData(alertString: String) : AlertData {
        var list = object : TypeToken<AlertData>(){}.type
        return gson.fromJson(alertString, list)
    }

    @TypeConverter
    fun fromDateToString(date:Date):String{
        return gson.toJson(date)
    }

    @TypeConverter
    fun fromStringToDate(dateString:String):Date{
        var date = object : TypeToken<Date>(){}.type
        return gson.fromJson(dateString,date)
    }
}