package com.example.crypto.helpers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.IBinder
import android.provider.Telephony
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.crypto.MainActivity


class ForegroundService : Service() {

    companion object {
        fun startService(context: Context, message: String) {
            val startIntent = Intent(context, ForegroundService::class.java)
            startIntent.putExtra("inputExtra", message)
            ContextCompat.startForegroundService(context, startIntent)
        }
    }

    private var isFirstRun = true
    private var serviceKilled = false

    private val chanelId = "Foreground Service ID"

    private lateinit var br: BroadcastReceiver

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (isFirstRun) {
            startForegroundService()
            isFirstRun = false
            serviceKilled = false
        } else {
            startBroadcast()
        }

        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    private fun startForegroundService() {

        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )

        Log.d("TTT", "МЫ СОЗДАЛИ ХУЙНЮ")

        val notification = Notification.Builder(this, chanelId)
            .setContentTitle("Foreground Service Kotlin Example")
            .setContentText("test")
            .setSmallIcon(com.example.crypto.R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)

        startForeground(1001, notification.build())

        startBroadcast()
    }

    private fun startBroadcast() {
        Log.e("BR", "startBroadcast")
        br = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                val bundle: Bundle? = p1?.extras
                val callingSIM = bundle?.getInt("simId", -1).toString()
                Log.e("Message", callingSIM)
                for (sms in Telephony.Sms.Intents.getMessagesFromIntent(p1)) {
                    Log.e("Message", sms.displayOriginatingAddress)
                    Toast.makeText(applicationContext, sms.displayOriginatingAddress, Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
        registerReceiver(br, IntentFilter("android.provider.Telephony.SMS_RECEIVED"))
    }

    override fun onDestroy() {
        try {
            unregisterReceiver(br)
        } catch (e: Exception) {
            Log.e("EEE", e.toString())
        }

        super.onDestroy()
    }

    private fun createNotificationChannel() {
        val chanelId = "Foreground Service ID"
        val serviceChannel = NotificationChannel(chanelId, "Foreground Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT)
        serviceChannel.setSound(null, null)
        val manager = getSystemService(NotificationManager::class.java)
        manager!!.createNotificationChannel(serviceChannel)
    }
}
