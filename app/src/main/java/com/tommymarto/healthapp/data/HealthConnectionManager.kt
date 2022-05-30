package com.tommymarto.healthapp.data

import android.content.Context
import android.os.Build
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.Permission
import androidx.health.connect.client.records.HeartRateSeries
import androidx.health.connect.client.records.Steps
import java.time.LocalDateTime

const val MIN_SUPPORTED_SDK = Build.VERSION_CODES.O_MR1

class HealthConnectionManager(context: Context) {
    val PERMISSIONS = setOf(
        Permission.createReadPermission(HeartRateSeries::class),
        Permission.createWritePermission(HeartRateSeries::class),
        Permission.createReadPermission(Steps::class),
        Permission.createWritePermission(Steps::class)
    )

    private val healthConnectClient = HealthConnectClient.getOrCreate(context)

    val availability = when {
        HealthConnectClient.isAvailable(context) -> HealthConnectAvailability.INSTALLED
        (Build.VERSION.SDK_INT >= MIN_SUPPORTED_SDK) -> HealthConnectAvailability.NOT_INSTALLED
        else -> HealthConnectAvailability.NOT_SUPPORTED
    }


    /**
     * Checks if all permissions are already granted so there's no need to request them
     */
    suspend fun hasAllPermissions(): Boolean {
        val grantedPermissions = healthConnectClient.permissionController.getGrantedPermissions(PERMISSIONS)
        return grantedPermissions.containsAll(PERMISSIONS)
    }

    suspend fun getDaySteps(day: LocalDateTime): Float {
        return (Math.random() * 110000).toFloat()
    }
    suspend fun getDayExercise(day: LocalDateTime): Float {
        return (Math.random() * 31).toFloat()
    }
    suspend fun getDayStand(day: LocalDateTime): Float {
        return (Math.random() * 12.5).toFloat()
    }
}

/**
 * Health Connect requires that the underlying Healthcore APK is installed on the device.
 * [HealthConnectAvailability] represents whether this APK is indeed installed, whether it is not
 * installed but supported on the device, or whether the device is not supported (based on Android
 * version).
 */
enum class HealthConnectAvailability {
    INSTALLED,
    NOT_INSTALLED,
    NOT_SUPPORTED
}