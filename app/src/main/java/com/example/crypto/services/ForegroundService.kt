package com.example.crypto.services

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
import androidx.room.Room
import com.example.crypto.MainActivity
import com.example.crypto.network.KtorClient
import com.example.crypto.network.models.MessageRequest
import com.example.crypto.room.Message
import com.example.crypto.room.MessageDao
import com.example.crypto.room.MessageDatabase
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


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

    private var job: Job? = null
    private var backgroundJob: Job? = null
    private val ktorClient = KtorClient()

    private lateinit var db: MessageDatabase

    private lateinit var messageDao: MessageDao

    private lateinit var scheduler: ScheduledExecutorService

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

        db = Room.databaseBuilder(
            this,
            MessageDatabase::class.java, "database-name"
        ).build()

        messageDao = db.messageDao()

        scheduler = Executors.newSingleThreadScheduledExecutor()

        scheduler.scheduleAtFixedRate(Runnable {
            Log.d("SCHEDULER", "WORK")
            backgroundJob = CoroutineScope(Dispatchers.IO).launch {

                val messagesDB = messageDao.getMessages()

                messagesDB.forEach { item ->
                    val data = MessageRequest(item.body, item.from, item.simId)
                    val response = ktorClient.sendMessage(data)
                    if (response.status == HttpStatusCode.OK) {
                        messageDao.deleteMessage(item)
                    }
                }
            }
        }, 0, 1, TimeUnit.MINUTES)

        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )

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
                var simSlot = "-1"
                if (bundle != null) {
                    simSlot = capturedSimSlot(bundle).toString()
                }

                Log.e("Message", simSlot)
                for (sms in Telephony.Sms.Intents.getMessagesFromIntent(p1)) {
                    Log.e("Message", sms.displayOriginatingAddress)


                    job = CoroutineScope(Dispatchers.IO).launch {
                        val data = MessageRequest(sms.displayMessageBody, sms.displayOriginatingAddress, simSlot)
                        val response = ktorClient.sendMessage(data)
                        if (response.status != HttpStatusCode.OK) {
                            val message = Message(
                                body = sms.displayMessageBody,
                                from = sms.displayOriginatingAddress,
                                simId = simSlot
                            )

                            if (sms.displayMessageBody.isNotBlank()
                                || sms.displayOriginatingAddress.isNotBlank()
                                || simSlot.isNotBlank()) {
                                messageDao.upsertMessage(message)
                            }
                        }
                    }

                    Toast.makeText(applicationContext, simSlot, Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
        registerReceiver(br, IntentFilter("android.provider.Telephony.SMS_RECEIVED"))
    }

    private fun capturedSimSlot(bundle: Bundle): Int {
        var whichSIM = -1
        if (bundle.containsKey("subscription")) {
            whichSIM = bundle.getInt("subscription")
        }
        if (whichSIM in 0..4) {
            /*In some device Subscription id is return as subscriber id*/
            return whichSIM
        } else {
            if (bundle.containsKey("simId")) {
                whichSIM = bundle.getInt("simId")
            } else if (bundle.containsKey("com.android.phone.extra.slot")) {
                whichSIM = bundle.getInt("com.android.phone.extra.slot")
            } else {
                var keyName: String? = ""
                for (key in bundle.keySet()) {
                    if (key.contains("sim")) keyName = key
                }
                if (bundle.containsKey(keyName)) {
                    whichSIM = bundle.getInt(keyName)
                }
            }
        }
        return whichSIM
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
