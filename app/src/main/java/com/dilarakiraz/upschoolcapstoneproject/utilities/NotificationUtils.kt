package com.dilarakiraz.upschoolcapstoneproject.utilities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import androidx.core.app.NotificationCompat
import com.dilarakiraz.upschoolcapstoneproject.R

object NotificationUtils {

    fun showNotification(context: Context){

        val builder: NotificationCompat.Builder

        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "CHANNEL_ID"
        val channelName = "CHANNEL_NAME"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)

            builder = NotificationCompat.Builder(context, channelId)
                .setContentTitle("Cherry Shopping")
                .setContentText("Sepetinde ürün unuttun!")
                .setSmallIcon(R.drawable.cherry)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
        }else{
            builder = NotificationCompat.Builder(context)
                .setContentTitle("Cherry Shopping")
                .setContentText("Sepetinde ürün unuttun!")
                .setSmallIcon(R.drawable.cherry)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
        }
        notificationManager.notify(1, builder.build())
    }
}