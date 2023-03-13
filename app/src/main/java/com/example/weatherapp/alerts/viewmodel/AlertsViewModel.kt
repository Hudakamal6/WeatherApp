package com.example.weatherapp.alerts.viewmodel

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.database.AlertState
import com.example.weatherapp.model.AlertData
import com.example.weatherapp.model.Repository
import com.example.weatherapp.model.Settings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AlertsViewModel(private val repo:Repository) :ViewModel(){


    var alertState = MutableStateFlow<AlertState>(AlertState.Loading)

    fun addAlert(alert: AlertData) {
        repo.insertAlert(alert)
        Log.i(ContentValues.TAG, "insert alerttt VIEWWWW MODELLLLLLLLLLLLLLLLLLLLLLLLLLLL ${alert}")
    }
    fun deleteAlert(alert: AlertData) {
        repo.deleteAlert(alert)
    }

    fun getAllAlert(): MutableStateFlow<AlertState> {
        var alertList = mutableListOf<AlertData>()
        viewModelScope.launch(Dispatchers.IO) {
            repo.storedAlerts().catch {
                alertState.value = AlertState.onFail(it)
                Log.i(TAG, "errrrroooooooooooooooooooooooooooooooor in viewwwwww model ${it.toString()}")
            }.collect{
              //  alertList.add(it)
                alertList = it as MutableList<AlertData>
                alertState.value = AlertState.onSuccessList(it)
                Log.i(ContentValues.TAG, "GET ALERT list IN VIEWW MODELLLLLLLLLLLLLLLLLLLL ${it} ")
            }

            Log.i(ContentValues.TAG, "GET ALERT IN VIEWW MODELLLLLLLLLLLLLLLLLLLL ${alertList}")
        }
        return alertState
        Log.i(ContentValues.TAG, "GET ALL IN VIEW MODELLLLLLLLLLLLLLLLLLLLLL RETURN ALERT STATE  ")
    }
    fun getStoredSettings(): Settings? {
        return repo.getSettingsSharedPreferences()
    }


}