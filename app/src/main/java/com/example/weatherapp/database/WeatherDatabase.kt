package com.example.weatherapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weatherapp.model.AlertData
import com.example.weatherapp.model.WeatherForecast
import com.example.weatherapp.utilities.WeatherConverter

@Database(entities = arrayOf(WeatherForecast::class,AlertData::class), version = 3)
@TypeConverters(WeatherConverter::class)

abstract class WeatherDatabase:RoomDatabase(){

    abstract fun weatherDao(): WeatherDAO


    companion object{
        private var weatherDatabase:WeatherDatabase? = null

        fun getInstance(context: Context):WeatherDatabase{
            return weatherDatabase ?: synchronized(this){
                val instance = Room.databaseBuilder(context.applicationContext, WeatherDatabase::class.java, "WeatherDatabase")
                    .fallbackToDestructiveMigration()
                    .build()
                weatherDatabase = instance
                instance
            }
        }
    }

//    abstract fun weatherDao():WeatherDAO
//
//    companion object{
//        private  var weatherDB : WeatherDatabase? = null
//        fun getinstance(context : Context):WeatherDatabase{
//            return weatherDB?: synchronized(this ){
//                weatherDB?: buildDatabase(context).also{
//                    weatherDB = it
//                }
//            }
//        }
//        private fun buildDatabase(context: Context): WeatherDatabase {
//            return Room.databaseBuilder(
//                context.applicationContext, WeatherDatabase::class.java, "DATABASE_NAME").fallbackToDestructiveMigration().build()
//
//        }
//    }
}