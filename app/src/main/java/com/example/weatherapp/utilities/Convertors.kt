package com.example.weatherapp.utilities

import java.text.SimpleDateFormat
import java.util.*

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
}
}