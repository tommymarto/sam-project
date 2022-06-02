package com.tommymarto.healthapp

import android.app.Application
import com.tommymarto.healthapp.data.db.DBClient
import com.tommymarto.healthapp.data.HealthConnectionManager

class App: Application() {
    val healthConnectManager by lazy { HealthConnectionManager(this) }
    val dbClient by lazy { DBClient(this) }
}