package com.example.weatherapp.database


import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.weatherapp.getOrAwaitValue
import com.example.weatherapp.model.AlertData
import com.example.weatherapp.model.CurrentWeather
import com.example.weatherapp.model.WeatherForecast
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class WeatherDaoTest {


    @get:Rule
   // var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: WeatherDatabase
    private lateinit var dao:WeatherDAO

    // method to intialize the data base
    @Before
    fun setUp(){
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext()
            ,WeatherDatabase::class.java).allowMainThreadQueries().build()
        dao = database.weatherDao()
    }

    // method to close the database
    @After
    fun tearDown(){
        database.close()
    }


    // method to test if the weather inserted successfully from database
    @Test
    fun insertWeather_toRoomDataBase_test(){

        val weather = WeatherForecast(33.3,35.55555,"",
            CurrentWeather(0,0.0,0,0,0,0.0, listOf())
            , listOf(),listOf(), listOf())
        dao.insert(weather)

        val getAllWeather = dao.getAllStoredFav()

        assert(getAllWeather.contains(weather))


    }

    // method to test if the weather deleted successfully from database
    @Test
    fun deleteWeather_fromRoomDataBase_test(){

        val weather = WeatherForecast(33.3,35.55555,"",
            CurrentWeather(0,0.0,0,0,0,0.0, listOf())
            , listOf(),listOf(), listOf())
        dao.deleteFavWeather(weather)

        val getAllWeather = dao.getAllStoredFav()

        assert(!getAllWeather.contains(weather))


    }

    // method to test if the alert inserted successfully from database
    @Test
    fun insertAlert_toRoomDataBase_test() = runBlocking {
        val alert = AlertData("address", "122", "133", "15/10","20/10",1,2,
            111111111111,
        "")
        dao.insertAlert(alert)

        val allShoppingItems = dao.getAllStoredAlerts().getOrAwaitValue()

        assertThat(allShoppingItems).contains(alert)
    }


    // method to test if the alert deleted successfully from database
    @Test
    fun deleteAlert_fromRoomDataBase_test() = runBlocking {
        val alert = AlertData("address", "122", "133", "15/10","20/10",1,2,
            111111111111,
            "")
        dao.insertAlert(alert)
        dao.deleteAlert(alert)

        val allShoppingItems = dao.getAllStoredAlerts().getOrAwaitValue()

        assertThat(allShoppingItems).doesNotContain(alert)
    }
}