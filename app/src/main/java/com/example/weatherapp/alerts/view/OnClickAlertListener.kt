package com.example.weatherapp.alerts.view

import com.example.weatherapp.model.Alert
import com.example.weatherapp.model.AlertData

interface OnClickAlertListener {

    fun onRemoveAlertClickListener(alert:AlertData)
}