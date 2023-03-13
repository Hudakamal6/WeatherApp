package com.example.weatherapp.database

import com.example.weatherapp.model.AlertData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

sealed class AlertState{
    class onSuccessList(val alertList : List<AlertData>):AlertState()
    class onSuccessWeather(val alert: AlertData):AlertState()
    class onFail(val msg: Throwable ):AlertState()
    object Loading : AlertState()
}
