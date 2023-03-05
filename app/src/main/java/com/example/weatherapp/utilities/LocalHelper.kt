package com.example.weatherapp.utilities

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import java.util.*

class LocalHelper(val context: Context) {
    companion object {
        fun setLocale(context: Context,lang:String) {
            update(context,lang)
        }

        fun update(context: Context, language: String) {
            updateResources(context, language)
            var appContext: Context = context.applicationContext
            if (context != appContext) {
                updateResources(appContext, language)
            }
        }


        @SuppressLint("ObsoleteSdkInt")
        fun updateResources(context: Context, language: String) {
            var locale = Locale(language)
            Locale.setDefault(locale)
            var resources: Resources = context.resources
            var config = Configuration(resources.configuration)
            if (Build.VERSION.SDK_INT >= 17) {
                config.setLocale(locale)
            } else {
                config.locale = locale
            }
            resources.updateConfiguration(config, resources.displayMetrics)
        }
    }
}