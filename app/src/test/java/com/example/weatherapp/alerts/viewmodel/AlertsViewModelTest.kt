package com.example.weatherapp.alerts.viewmodel

import com.example.weatherapp.getOrAwaitValue
import com.example.weatherapp.model.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class AlertsViewModelTest{

    private lateinit var repository: RepositoryInterface
    private lateinit var alertViewModel: FakeRepository
    private lateinit var alert:AlertData
    private lateinit var alertList: MutableList<AlertData>

    @Before
    fun initAlertsViewModelTest() {
        // fake Data
        alertList = mutableListOf<AlertData>(
            AlertData(
                "address", "122", "133", "15/10", "20/10", 1, 2,
                111111111111,
                ""
            ),
            AlertData(
                "address", "122", "133", "15/10", "20/10", 1, 2,
                111111111111,
                ""
            ),
            AlertData(
                "address", "122", "133", "15/10", "20/10", 1, 2,
                111111111111,
                ""
            ),
            AlertData(
                "address", "122", "133", "15/10", "20/10", 1, 2,
                111111111111,
                ""
            ),
            AlertData(
                "address", "122", "133", "15/10", "20/10", 1, 2,
                111111111111,
                ""
            ),
            AlertData(
                "address", "122", "133", "15/10", "20/10", 1, 2,
                111111111111,
                ""
            ),
        )
        repository = FakeRepository()
        alertViewModel = FakeRepository()
        alert =  AlertData(
            "address", "122", "133", "15/10", "20/10", 1, 2,
            111111111111,
            ""
        )

    }

    @Test
    fun insertAlert_insertItem_increaseSizeOfList() = runBlocking {

        //Given
        alertViewModel.insertAlert(alert)
        //When
        alertViewModel.getAllAlertsInRepo().getOrAwaitValue()
        alertList.add(alert)
        //Then
       assertThat(alertList.size,`is`(5))
    }

    @Test
    fun deleteAlert_deleteItem_decreaseSizeOfList() = runBlocking {

        //Given
        alertViewModel.deleteAlert(alert)
        //When
        alertViewModel.getAllAlertsInRepo().getOrAwaitValue()

            alertList.remove(alert)

        //Then
       assertThat(alertList.size,`is`(4))
    }
}
