package com.tommymarto.healthapp

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.work.*
import com.tommymarto.healthapp.utils.NOTIFICATION_ID
import com.tommymarto.healthapp.utils.createNotificationChannel
import com.tommymarto.healthapp.utils.initNotification
import java.util.concurrent.TimeUnit

class ActivityForegroundService: Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel(this)
        val notification = initNotification(this)
        startForeground(NOTIFICATION_ID, notification)

        scheduleUpdates()

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    var isWorkScheduled = false
    private fun scheduleUpdates() {
        if (!isWorkScheduled) {
            isWorkScheduled = true

            val workManager = WorkManager.getInstance(this)
            workManager.cancelAllWork()

//            val workRequest = PeriodicWorkRequestBuilder<DataWorker>(
//                PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS + 1, TimeUnit.MILLISECONDS
//            ).build()
//            workManager.enqueue(workRequest)

            (0..100).forEach {
                val work = OneTimeWorkRequestBuilder<DataWorker>()
                    .setInitialDelay(it * 1L, TimeUnit.SECONDS)
                    .setInputData(workDataOf("index" to it))
                    .build()

                workManager.enqueue(work)
            }
        }
    }
}