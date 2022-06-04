package com.tommymarto.healthapp.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.widget.RemoteViews
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.res.ResourcesCompat
import com.tommymarto.healthapp.MainActivity
import com.tommymarto.healthapp.R

const val NOTIFICATION_ID = 0
const val CHANNEL_ID = "dailyprogress"
const val CHANNEL_NAME = "Daily Progress"
const val CHANNEL_DESCRIPTION = "Shows the current status towards the daily goal"

fun createChannel(context: Context) {
    val notificationManager = context.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
    val importance = NotificationManager.IMPORTANCE_LOW
    val mChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
    mChannel.description = CHANNEL_DESCRIPTION
    notificationManager.createNotificationChannel(mChannel)
}

fun notifyNotification(context: Context, id: Int, notification: Notification) {
    NotificationManagerCompat.from(context).notify(id, notification)
}

fun createNotification(context: Context): Notification {
    val bitmap = createTodayChart(context)

    val notificationLayout = RemoteViews(context.packageName, R.layout.notification_layout)
    notificationLayout.setImageViewBitmap(R.id.notification_image, bitmap)

    val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
    return notificationBuilder
        .setSmallIcon(R.drawable.heart)
        .setStyle(NotificationCompat.DecoratedCustomViewStyle())
        .setCustomContentView(notificationLayout)
        .setColorized(true)
        .setColor(0xFF000000.toInt())
        .setOngoing(true)
        .build()

//    notificationManager.notify(0, notification)
}

private fun createTodayChart(context: Context): Bitmap {
    val steps = 7124F
    val exercise = 23F
    val distance = 5123F

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

    return bitmap
}