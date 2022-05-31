package com.tommymarto.healthapp.data

import android.content.Context
import android.os.Build
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.Permission
import androidx.health.connect.client.records.ActivitySession
import androidx.health.connect.client.records.Distance
import androidx.health.connect.client.records.HeartRateSeries
import androidx.health.connect.client.records.Steps
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.LocalDateTime

const val MIN_SUPPORTED_SDK = Build.VERSION_CODES.O_MR1

class HealthConnectionManager(context: Context) {
    val PERMISSIONS = setOf(
        Permission.createReadPermission(Distance::class),
        Permission.createWritePermission(Distance::class),
        Permission.createReadPermission(ActivitySession::class),
        Permission.createWritePermission(ActivitySession::class),
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

    suspend fun getDayActivityDetailed(day: LocalDateTime): List<ActivitySession> {
        val beginningOfDay = day.withHour(0).withMinute(0).withSecond(0)
        val endOfDay = day.withHour(23).withMinute(59).withSecond(59)

        val request = ReadRecordsRequest(
            recordType = ActivitySession::class,
            timeRangeFilter = TimeRangeFilter.Companion.between(beginningOfDay, endOfDay)
        )

        val response = healthConnectClient.readRecords(request)
        return response.records
    }

    suspend fun getDayActivity(day: LocalDateTime): DayActivity {
        val beginningOfDay = day.withHour(0).withMinute(0).withSecond(0)
        val endOfDay = day.withHour(23).withMinute(59).withSecond(59)

        val aggregateDataTypes = setOf(
            Steps.COUNT_TOTAL,
            ActivitySession.ACTIVE_TIME_TOTAL,
            Distance.DISTANCE_TOTAL
        )

        val request = AggregateRequest(
            metrics = aggregateDataTypes,
            timeRangeFilter = TimeRangeFilter.Companion.between(beginningOfDay, endOfDay)
        )

        val response = healthConnectClient.aggregate(request)
        val steps = response.getMetric(Steps.COUNT_TOTAL)
        val activeTime = response.getMetric(ActivitySession.ACTIVE_TIME_TOTAL)?.seconds?.toFloat()?.div(60)
        val distance = response.getMetric(Distance.DISTANCE_TOTAL)?.toLong()

        return DayActivity(
             steps ?: 0,
             activeTime ?: 0F,
             distance ?: 0
        )
    }

    suspend fun getDayExercise(day: LocalDateTime): Float {
        return (Math.random() * 31).toFloat()
    }
    suspend fun getDayStand(day: LocalDateTime): Float {
        return (Math.random() * 12.5).toFloat()
    }

    data class DayActivity(
        val steps: Long,
        val activeTime: Float,
        val distance: Long
    )
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