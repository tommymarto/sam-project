package com.tommymarto.healthapp.data.db

import android.content.Context
import androidx.room.Room
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class DBClient(context: Context) {
    private val db = Room.databaseBuilder(context, AppDatabase::class.java, "db").build()

    suspend fun getDayPath(day: LocalDateTime): List<LatLng> {
        return withContext(Dispatchers.IO) {
            val pointDao = db.pointDao()
            return@withContext pointDao.getByDay(day.toLocalDate()).map { LatLng(it.latitude, it.longitude) }
        }
    }

    suspend fun insertDay(day: LocalDateTime, path: List<LatLng>) {
        withContext(Dispatchers.IO) {
            val pointDao = db.pointDao()
            val points = path.map {
                Point(
                    day = day.toLocalDate(),
                    latitude = it.latitude,
                    longitude = it.longitude
                )
            }

            pointDao.insert(*points.toTypedArray())
        }
    }
}