package com.tommymarto.healthapp

import android.app.Application
import androidx.health.connect.client.permission.Permission
import androidx.health.connect.client.records.HeartRateSeries
import androidx.health.connect.client.records.Steps
import com.tommymarto.healthapp.data.HealthConnectionManager

class App: Application() {
    val PERMISSIONS = setOf(
        Permission.createReadPermission(HeartRateSeries::class),
        Permission.createWritePermission(HeartRateSeries::class),
        Permission.createReadPermission(Steps::class),
        Permission.createWritePermission(Steps::class)
    )

    val healthConnectManager by lazy { HealthConnectionManager(this) }
}