package com.tommymarto.healthapp.data

import android.content.Context
import android.os.Build
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.Permission

const val MIN_SUPPORTED_SDK = Build.VERSION_CODES.O_MR1

class HealthConnectionManager(context: Context) {
    private val healthConnectClient = HealthConnectClient.getOrCreate(context)

    val availability = when {
        HealthConnectClient.isAvailable(context) -> HealthConnectAvailability.INSTALLED
        (Build.VERSION.SDK_INT >= MIN_SUPPORTED_SDK) -> HealthConnectAvailability.NOT_INSTALLED
        else -> HealthConnectAvailability.NOT_SUPPORTED
    }


    /**
     * Checks if all permissions are already granted so there's no need to request them
     */
    suspend fun hasAllPermissions(permissions: Set<Permission>): Boolean {
        return permissions == healthConnectClient.permissionController.getGrantedPermissions(permissions)
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