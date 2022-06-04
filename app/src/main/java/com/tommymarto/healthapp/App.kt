package com.tommymarto.healthapp

import android.app.Application
import androidx.work.Configuration
import com.tommymarto.healthapp.data.db.DBClient
import com.tommymarto.healthapp.data.HealthConnectionManager

class App: Application(), Configuration.Provider {
    val healthConnectManager by lazy { HealthConnectionManager(this) }
    val dbClient by lazy { DBClient(this) }

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .build()
}