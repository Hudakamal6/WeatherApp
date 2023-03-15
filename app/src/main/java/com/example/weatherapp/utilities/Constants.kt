package com.example.weatherapp.utilities

object Constants {

    const val NOTIFICATION_ID = "appName_notification_id"
    const val NOTIFICATION_NAME = "appName"
    const val NOTIFICATION_CHANNEL = "appName_channel_01"
    const val NOTIFICATION_WORK = "appName_notification_work"

    const val MY_SHARED_PREFERENCES = "WeatherSP"
    const val MY_CURRENT_LOCATION = "currentLocationSP"
    const val MY_SETTINGS_PREFS = "SettingsSP"

    enum class units(val unitsValue: Int) {
        standard (0),
        metric (1),
        imperial(2)
    }

    enum class languages(val langValue: String) {
        en ("en"),
        ar("ar")
    }

    const val weatherKey = ""

    val arrayOfUnits = arrayOf("standard","metric","imperial")
    val arrayOfLangs = arrayOf("en","ar")
}


//const val DATABASE_NAME = "Weather Database"
//const val MY_SHARED_PREFERENCES = "WeatherSharedPrefs"
//const val MY_CURRENT_WEATHER_OBJ = "currentWeatherPrefs"
//const val MY_SETTINGS_PREFS = "SettingsSharedPref"
//
//const val NOTIFICATION_ID = "appName_notification_id"
//const val NOTIFICATION_NAME = "appName"
//const val NOTIFICATION_CHANNEL = "appName_channel_01"
//const val NOTIFICATION_WORK = "appName_notification_work"
//
//const val ENGLISH = true
//const val ARABIC = false
//const val STANDARD = 0
//const val METRIC = 1
//const val IMPERIAL = 2
//const val ENABLED = true
//const val DISABLED = false
//const val NONE = 0
//const val GPS = 1
//const val MAPS = 2
//
//val arrayOfUnits = arrayOf("standard","metric","imperial")
//val arrayOfLangs = arrayOf("en","ar")