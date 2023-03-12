package com.example.weatherapp.alerts

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.work.ListenableWorker.Result.success
import com.example.weatherapp.R
import com.example.weatherapp.alerts.view.AlertsFragment
import com.example.weatherapp.database.LocalSource
import com.example.weatherapp.model.Repository
import com.example.weatherapp.model.Settings
import com.example.weatherapp.model.WeatherForecast
import com.example.weatherapp.network.ApiInterface
import com.example.weatherapp.network.RemoteSource
import com.example.weatherapp.network.RetroFitClient
import com.example.weatherapp.utilities.Constants
import com.example.weatherapp.utilities.Constants.MY_SHARED_PREFERENCES
import com.example.weatherapp.utilities.Constants.NOTIFICATION_CHANNEL
import com.example.weatherapp.utilities.Constants.NOTIFICATION_ID
import com.example.weatherapp.utilities.Constants.NOTIFICATION_NAME
import com.example.weatherapp.utilities.CurrentUser
import com.example.weatherapp.utilities.vectorToBitmap
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch



class WeatherWorker(var context: Context, var params: WorkerParameters):Worker(context,params) {

    private var settings: Settings? = null
    private lateinit var repo: Repository


    override fun doWork(): Result {
        repo = Repository.getInstance(
            RemoteSource.getInstance(),
            LocalSource.getInstance(context),
            context,
            context.getSharedPreferences(MY_SHARED_PREFERENCES, Context.MODE_PRIVATE)
        )
        settings = repo.getSettingsSharedPreferences()

        if (settings?.notification as Boolean) {
            val id = inputData.getLong(NOTIFICATION_ID, 0).toInt()

            var message = inputData.getString("CurrentUser.message").toString()
            var lat = CurrentUser.alertLoc.latitude
            var long = CurrentUser.alertLoc.longitude

            var coroutine = CoroutineScope(Dispatchers.IO)


            coroutine.launch {
                val retrofit = RetroFitClient.getInstance()
                val service = retrofit.create(ApiInterface::class.java)
                var messagee = ""
                val response = service.getTheWholeWeather(
                    lat,
                    long, repo.getSettingsSharedPreferences()?.unit.toString(),
                    Constants.languages.en.langValue, "minutely",
                    "992213628dbceb7e7fb06cf59035697d"
                )
                if (response.isSuccessful) {
                    var eventWeather = "NO Weather Alerts Until Now "
                    if (response.body()?.alerts != null) {
                        eventWeather =
                            " Weather Event Condition is ${response.body()?.alerts?.get(0)?.event.toString()}"
                    }

                    Log.i(
                        ContentValues.TAG,
                        "alertttt notifiiiiiiiiiiiiiiicationnnnnn ${response.body()}"
                    )
                    messagee = eventWeather

                }

                val intent = Intent(applicationContext, AlertsFragment::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.putExtra(NOTIFICATION_ID, id)

                val notificationManager =
                    applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                val bitmap = applicationContext.vectorToBitmap(R.mipmap.weatherlogo)
                val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.getActivity(
                        applicationContext,
                        0,
                        intent,
                        PendingIntent.FLAG_MUTABLE
                    )
                } else {
                    PendingIntent.getActivity(
                        applicationContext,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                }
                val notification =
                    NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL)
                        .setLargeIcon(bitmap).setSmallIcon(R.mipmap.weatherlogo)
                        .setContentTitle("Weather App").setContentText(messagee)
                        .setDefaults(NotificationCompat.DEFAULT_ALL).setContentIntent(pendingIntent)
                        .setAutoCancel(true)

                notification.priority = NotificationCompat.PRIORITY_MAX

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notification.setChannelId(NOTIFICATION_CHANNEL)

                    val ringtoneManager =
                        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    val audioAttributes =
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()

                    val channel =
                        NotificationChannel(
                            NOTIFICATION_CHANNEL, NOTIFICATION_NAME,
                            NotificationManager.IMPORTANCE_HIGH
                        )

                    channel.enableLights(true)
                    channel.lightColor = Color.RED
                    channel.enableVibration(true)
                    channel.vibrationPattern =
                        longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                    channel.setSound(ringtoneManager, audioAttributes)
                    notificationManager.createNotificationChannel(channel)
                }

                notificationManager.notify(id, notification.build())
            }
        }
        return success()

    }
}