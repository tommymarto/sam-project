package com.tommymarto.healthapp

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.tommymarto.healthapp.utils.NOTIFICATION_ID
import com.tommymarto.healthapp.utils.notifyNotification
import com.tommymarto.healthapp.utils.updateNotification

class DataWorker(
    private val context: Context,
    workerParams: WorkerParameters
): CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val index = inputData.getInt("index", 100)
        val notification = updateNotification(context, index)
        notifyNotification(context, NOTIFICATION_ID, notification)

        return Result.success()
    }
}