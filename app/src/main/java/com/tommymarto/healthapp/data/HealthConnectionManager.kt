package com.tommymarto.healthapp.data

import android.content.Context
import android.os.Build
import android.text.format.DateUtils
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.Permission
import androidx.health.connect.client.records.*
import androidx.health.connect.client.request.AggregateGroupByDurationRequest
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.tommymarto.healthapp.utils.isToday
import java.time.*
import java.time.temporal.ChronoUnit
import kotlin.random.Random

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

    suspend fun generateDataIfNotPresent(day: LocalDateTime) {
        val oldData = getDayActivity(day)
        if (oldData.steps != 0L || oldData.activeTime != 0F || oldData.distance != 0L) {
            return
        }

        val numSessions = Random.nextInt(20)
        val sessions = (0..numSessions).flatMap {
            val beginningOfDay = day.withHour(0).withMinute(0).withSecond(0)
            val endOfDay = day.withHour(23).withMinute(59).withSecond(59)
            val offset = Random.nextDouble()
            val timezoneOffset = OffsetDateTime.now().offset
            var start = if (day.isToday()) {
                beginningOfDay.plusSeconds(
                    (Duration.between(beginningOfDay, LocalDateTime.now().minusHours(1)).seconds * offset).toLong()
                ).toInstant(timezoneOffset)
            } else {
                beginningOfDay.plusSeconds(
                    (Duration.between(beginningOfDay, endOfDay.minusHours(1)).seconds * offset).toLong()
                ).toInstant(timezoneOffset)
            }
            val end = start.plusSeconds(60L + Random.nextLong(220L))

            listOf(
                ActivitySession(
                    startTime = start,
                    startZoneOffset = timezoneOffset,
                    endTime = end,
                    endZoneOffset = timezoneOffset,
                    activityType = ActivitySession.ActivityType.RUNNING,
                    title = "My Run #${Random.nextInt(0, 60)}"
                ),
                Steps(
                    startTime = start,
                    startZoneOffset = timezoneOffset,
                    endTime = end,
                    endZoneOffset = timezoneOffset,
                    count = (200 + 200 * Random.nextInt(5)).toLong()
                ),
                Distance(
                    startTime = start,
                    startZoneOffset = timezoneOffset,
                    endTime = end,
                    endZoneOffset = timezoneOffset,
                    distanceMeters = (100 + 100 * Random.nextInt(5)).toDouble()
                )
            )
        }

        healthConnectClient.insertRecords(sessions)

    }

    suspend fun getDayActivityDetailed(day: LocalDateTime): List<HealthActivity> {
        val beginningOfDay = day.withHour(0).withMinute(0).withSecond(0)
        val endOfDay = day.withHour(23).withMinute(59).withSecond(59)

        val aggregateDataTypes = setOf(
            Steps.COUNT_TOTAL,
            ActivitySession.ACTIVE_TIME_TOTAL,
            Distance.DISTANCE_TOTAL
        )

        val request = AggregateGroupByDurationRequest(
            metrics = aggregateDataTypes,
            timeRangeFilter = TimeRangeFilter.Companion.between(beginningOfDay, endOfDay),
            timeRangeSlicer = Duration.ofMinutes(30)
        )

        val response = healthConnectClient.aggregateGroupByDuration(request)

        val list = MutableList(48) { HealthActivity(0, 0F, 0) }

        response.forEach { bucket ->
            val secondsFromBeginningOfDay = (bucket.startTime.epochSecond - beginningOfDay.toEpochSecond(OffsetDateTime.now().offset)).toInt()

            val steps = bucket.result.getMetric(Steps.COUNT_TOTAL)
            val activeTime = bucket.result.getMetric(ActivitySession.ACTIVE_TIME_TOTAL)?.seconds?.toFloat()?.div(60)
            val distance = bucket.result.getMetric(Distance.DISTANCE_TOTAL)?.toLong()

            list[(secondsFromBeginningOfDay / 60) / 30] = HealthActivity(
                steps ?: 0,
                activeTime ?: 0F,
                distance ?: 0
            )
        }

        return list
    }

    suspend fun getDayActivity(day: LocalDateTime): HealthActivity {
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

        return HealthActivity(
             steps ?: 0,
             activeTime ?: 0F,
             distance ?: 0
        )
    }

    data class HealthActivity(
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