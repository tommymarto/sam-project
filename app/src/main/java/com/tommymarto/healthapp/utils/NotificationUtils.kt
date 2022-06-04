package com.tommymarto.healthapp.utils

import android.app.*
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.widget.RemoteViews
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.tommymarto.healthapp.App
import com.tommymarto.healthapp.R
import com.tommymarto.healthapp.data.HealthConnectionManager
import java.time.LocalDateTime

const val NOTIFICATION_ID = 1
const val CHANNEL_ID = "dailyprogress"
const val CHANNEL_NAME = "Daily Progress"
const val CHANNEL_DESCRIPTION = "Shows the current status towards the daily goal"

private var _application: App? = null
private val application: App get() = _application!!

fun setApplication(application: App) {
    _application = application
}

private var _notificationManager: NotificationManager? = null
private val notificationManager get() = _notificationManager!!

fun createNotificationChannel(context: Context) {
    if (_notificationManager == null) {
        _notificationManager = context.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
        val importance = NotificationManager.IMPORTANCE_LOW
        val mChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
        mChannel.description = CHANNEL_DESCRIPTION
        notificationManager.createNotificationChannel(mChannel)
    }
}

fun notifyNotification(context: Context, id: Int, notification: Notification) {
    NotificationManagerCompat.from(context).notify(id, notification)
}

suspend fun updateNotification(context: Context, index: Int): Notification {
    val (bitmap, activity) = createTodayChart(context, index)

    fun avgCompletion(activity: HealthConnectionManager.HealthActivity): Float {
        return listOf(
            activity.steps.div(10000F),
            activity.activeTime.div(30F),
            activity.distance.div(6000F)
        ).average().toFloat()
    }

    val text = when (avgCompletion(activity)) {
        in 0.95F..Float.MAX_VALUE -> "Enough work today! Enjoy your rest!"
        in 0.5F..0.95F -> "You're almost there! Do your best!"
        else -> "Make sure to fill your circles today!"
    }

    val layout = createNotificationLayout(context, bitmap, text)
    return createNotification(context, layout)
}

fun initNotification(context: Context): Notification {
    val bitmapSize = 512
    val bitmap = Bitmap.createBitmap(bitmapSize, bitmapSize, Bitmap.Config.ARGB_8888)

    val layout = createNotificationLayout(context, bitmap, "")
    return createNotification(context, layout)
}

private fun createNotificationLayout(context: Context, bitmap: Bitmap, text: String): RemoteViews {
    val notificationLayout = RemoteViews(context.packageName, R.layout.notification_layout)
    notificationLayout.setImageViewBitmap(R.id.notificationImage, bitmap)
    notificationLayout.setTextViewText(R.id.textPhrase, text)

    return notificationLayout
}

private fun createNotification(context: Context, notificationLayout: RemoteViews): Notification {
    val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
    return notificationBuilder
        .setSmallIcon(R.drawable.heart)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
        .setOnlyAlertOnce(true)
        .setStyle(NotificationCompat.DecoratedCustomViewStyle())
        .setCustomContentView(notificationLayout)
        .setColorized(true)
        .setColor(0xFF000000.toInt())
        .setOngoing(true)
        .build()

}

private suspend fun createTodayChart(context: Context, index: Int): Pair<Bitmap, HealthConnectionManager.HealthActivity> {
    val activity = application.healthConnectManager.getDayActivity(LocalDateTime.now())
    val steps = activity.steps.toFloat() * index / 100
    val exercise = activity.activeTime * index / 100
    val distance = activity.distance.toFloat() * index / 100

    val values = listOf(steps, exercise, distance)

    val bitmapSize = 512
    val bitmap = Bitmap.createBitmap(bitmapSize, bitmapSize, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val paint = Paint()
    paint.strokeCap = Paint.Cap.ROUND
    paint.style = Paint.Style.STROKE
    paint.isAntiAlias = true

    data class Donut(
        val radius: Float,
        val properties: DonutChartProperties
    )

    val padding = 10F
    val spacing = 5F
    val thickness = 50F
    val colors = listOf(
        Donut(
            bitmapSize.div(3F) - padding,
            DonutChartProperties(
                context.resources.getColor(R.color.brightDarkRed, null),
                context.resources.getColor(R.color.backgroundDarkRed, null),
                10000F,
                thickness
            )
        ),
        Donut(
            bitmapSize.div(3F) - padding - thickness - spacing,
            DonutChartProperties(
                context.resources.getColor(R.color.brightGreen, null),
                context.resources.getColor(R.color.backgroundGreen, null),
                30F,
                thickness
            )
        ),
        Donut(
            bitmapSize.div(3F) - padding - 2 * thickness - 2 * spacing,
            DonutChartProperties(
                context.resources.getColor(R.color.brightCyan, null),
                context.resources.getColor(R.color.backgroundCyan, null),
                6000F,
                thickness
            )
        ),
    )

//    canvas.drawARGB(255, 0, 0, 0)
    (colors zip values).forEach {
        val padding = it.first.properties.thickness
        val center = bitmapSize.div(2F)
        val min = center - it.first.radius - (padding / 2)
        val max = center + it.first.radius + (padding / 2)
        val rect = RectF(min, min, max, max)

        paint.strokeWidth = it.first.properties.thickness

        paint.color = it.first.properties.background
        canvas.drawArc(rect, 90F, 360F, false, paint)

        paint.color = it.first.properties.color
        val sweep = java.lang.Float.min(360F, (it.second / it.first.properties.max) * 360F)
        canvas.drawArc(rect, 90F, sweep, false, paint)
    }

    return bitmap to HealthConnectionManager.HealthActivity(
        steps.toLong(), exercise, distance.toLong()
    )
}