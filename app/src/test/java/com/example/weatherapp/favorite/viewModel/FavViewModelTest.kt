package com.example.weatherapp.favorite.viewModel


import com.example.weatherapp.model.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test


@ExperimentalCoroutinesApi
class FavoriteViewModelTest {

    private lateinit var repository: RepositoryInterface
    private lateinit var favouriteViewModel: FakeRepository
    private lateinit var favWeather: WeatherForecast
    private lateinit var favList: MutableList<WeatherForecast>

    @Before
    fun initFavouriteViewModelTest() {
        // fake Data
        favList = mutableListOf<WeatherForecast>(
            WeatherForecast(
                33.3, 35.55555, "",
                CurrentWeather(0, 0.0, 0, 0, 0, 0.0, listOf()), listOf(), listOf(), listOf()
            ),
            WeatherForecast(
                33.3, 35.55555, "",
                CurrentWeather(0, 0.0, 0, 0, 0, 0.0, listOf()), listOf(), listOf(), listOf()
            ),
            WeatherForecast(
                33.3, 35.55555, "",
                CurrentWeather(0, 0.0, 0, 0, 0, 0.0, listOf()), listOf(), listOf(), listOf()
            ),
            WeatherForecast(
                33.3, 35.55555, "",
                CurrentWeather(0, 0.0, 0, 0, 0, 0.0, listOf()), listOf(), listOf(), listOf()
            )
        )
        repository = FakeRepository()
        favouriteViewModel = FakeRepository()
        favWeather = WeatherForecast(
            33.3, 35.55555, "",
            CurrentWeather(0, 0.0, 0, 0, 0, 0.0, listOf()), listOf(), listOf(), listOf()
        )

    }

    @Test
    fun insertFavouritePlace_insertItem_increaseSizeOfList() = runBlocking {

        //Given
        favouriteViewModel.insertFavoriteWeather(favWeather)
        //When
        favouriteViewModel.storedLocations().collect {

            favList.add(it)
        }
        //Then
        assertThat(favList.size, `is`(5))
    }

@Test
fun deleteItemFavWeather_deleteItem_decreaseSizeOfList() = runBlocking {

    //Given
    favouriteViewModel.deleteFavoriteWeather(favWeather)
    //When
    favouriteViewModel.storedLocations().collect {

        favList.remove(it)
    }
    //Then
    assertThat(favList.size, `is`(4))
}
}