package com.tommymarto.healthapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.RemoteViews
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.WindowCompat
import androidx.core.view.drawToBitmap
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.db.williamchart.view.DonutChartView
import com.tommymarto.healthapp.databinding.ActivityMainBinding
import com.tommymarto.healthapp.ui.components.ActivityDonutChartView
import com.tommymarto.healthapp.utils.DonutChartProperties
import com.tommymarto.healthapp.utils.fillDonutChart
import com.tommymarto.healthapp.utils.toBitmap


class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onStart() {
        super.onStart()

        createNotification()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.shareButton -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    fun updateTitle(title: String) {
        binding.toolbar.title = title
    }

    private fun createNotification() {
        val name = "Daily Progress"
        val descriptionText = "Shows the current status towards the step goal"
        val importance = NotificationManager.IMPORTANCE_LOW
        val channelId = "dailyprogress"
        val mChannel = NotificationChannel(channelId, name, importance)
        mChannel.description = descriptionText
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)

        R.layout.activity_donut_chart_view

        // setup the layout to use it in custom notification
        val notificationLayout = RemoteViews(this.packageName, R.layout.notification_layout)

        val activityDonut = ActivityDonutChartView(this, null)

        val donut = DonutChartView(this)
        fillDonutChart(donut, 10F, DonutChartProperties(
            resources.getColor(R.color.brightDarkRed, theme),
            resources.getColor(R.color.backgroundDarkRed, theme),
            30F,
            15F
        ))

//        val largeIcon = activityDonut.drawToBitmap()

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
//        val d = ResourcesCompat.getDrawable(resources, R.drawable.day_letter_background, theme)
//        val largeIcon = d!!.toBitmap()
        val largeIcon = donut.toBitmap(50, 50)
        val notification = notificationBuilder
            .setSmallIcon(R.drawable.day_letter_background)
//            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
//            .setCustomContentView(notificationLayout)
            .setContentText("boia")
            .setLargeIcon(largeIcon)
            .setOngoing(true)
            .build()

        println("largeIcon: $largeIcon")

        notificationManager.notify(0, notification)
    }


}