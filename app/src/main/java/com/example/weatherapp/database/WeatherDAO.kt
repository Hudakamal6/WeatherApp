package com.example.weatherapp.database



import androidx.room.*
import com.example.weatherapp.model.AlertData
import com.example.weatherapp.model.WeatherForecast


@Dao
interface WeatherDAO {
    @Query("SELECT * FROM weathers")
    fun getAllStoredFav():List<WeatherForecast>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(weather:WeatherForecast)

    @Delete
    fun deleteFavWeather(weather: WeatherForecast)

    @Query("SELECT * FROM weathers WHERE lat == :latt AND lon == :longg")
    fun searchWithLatLong(latt:Double, longg:Double): WeatherForecast

    @Query("SELECT * FROM alerts")
    fun getAllStoredAlerts():List<AlertData>

    @Insert(onConflict =OnConflictStrategy.REPLACE)
    fun insertAlert(alert: AlertData)

    @Delete
    fun deleteAlert(alert:AlertData)

}
