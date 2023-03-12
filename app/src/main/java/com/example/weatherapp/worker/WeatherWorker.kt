package com.example.weatherapp.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import android.media.RingtoneManager
import android.net.Uri
import com.example.weatherapp.R
import com.example.weatherapp.alerts.view.AlertsFragment
import com.example.weatherapp.database.LocalSource
import com.example.weatherapp.model.Repository
import com.example.weatherapp.model.Settings
import com.example.weatherapp.network.RemoteSource
import com.example.weatherapp.utilities.Constants.MY_SHARED_PREFERENCES
import com.example.weatherapp.utilities.Constants.NOTIFICATION_CHANNEL
import com.example.weatherapp.utilities.Constants.NOTIFICATION_ID
import com.example.weatherapp.utilities.Constants.NOTIFICATION_NAME
import com.example.weatherapp.utilities.vectorToBitmap


class WeatherWorker(var context: Context, var params: WorkerParameters):Worker(context,params) {

    private var settings: Settings? = null
    private lateinit var repo: Repository

    override fun doWork(): Result {
        repo = Repository.getInstance(
            RemoteSource.getInstance(),
            LocalSource.getInstance(context),
            context,
            context.getSharedPreferences(MY_SHARED_PREFERENCES, Context.MODE_PRIVATE))
        settings = repo.getSettingsSharedPreferences()

        if(settings?.notification as Boolean) {
            val id = inputData.getLong(NOTIFICATION_ID, 0).toInt()
            sendNotification(id)
        }

        return Result.success()
    }

    private fun sendNotification(id: Int) {
        val intent = Intent(applicationContext, AlertsFragment::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(NOTIFICATION_ID, id)

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val bitmap = applicationContext.vectorToBitmap(R.mipmap.weatherlogo)
        val titleNotification = applicationContext.getString(R.string.chanelName)
        val subtitleNotification = applicationContext.getString(R.string.channel_description)
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)
        val uri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notification = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL)
            .setLargeIcon(bitmap)
            .setSmallIcon(R.mipmap.weatherlogo)
            .setContentTitle(titleNotification)
            .setContentText(subtitleNotification)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSound(uri)

        notification.priority = NotificationCompat.PRIORITY_MAX

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification.setChannelId(NOTIFICATION_CHANNEL)

            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL, NOTIFICATION_NAME, NotificationManager.IMPORTANCE_HIGH)

            channel.enableLights(true)
            channel.lightColor = Color.RED
            //channel.enableVibration(true)
            //channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(id, notification.build())
    }
}