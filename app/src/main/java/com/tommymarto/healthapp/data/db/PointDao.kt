package com.tommymarto.healthapp.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import java.time.LocalDate

@Dao
interface PointDao {
    @Query("SELECT * FROM point WHERE day = (:day)")
    fun getByDay(day: LocalDate): List<Point>

    @Insert
    fun insert(vararg points: Point)
}