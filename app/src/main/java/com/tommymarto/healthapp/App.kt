package com.tommymarto.healthapp

import android.app.Application
import androidx.health.connect.client.permission.Permission
import androidx.health.connect.client.records.HeartRateSeries
import androidx.health.connect.client.records.Steps
import com.tommymarto.healthapp.data.HealthConnectionManager

class App: Application() {
    val healthConnectManager by lazy { HealthConnectionManager(this) }
}