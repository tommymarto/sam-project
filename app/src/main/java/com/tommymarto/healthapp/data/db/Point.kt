package com.tommymarto.healthapp.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.time.LocalDate

@Entity
data class Point(
    val day: LocalDate,
    val latitude: Double,
    val longitude: Double
) {
    @PrimaryKey(autoGenerate = true) var uid: Int = 0
}

class DateConverter {
    @TypeConverter
    fun toDate(dateLong: Long): LocalDate {
        return LocalDate.ofEpochDay(dateLong)
    }

    @TypeConverter
    fun fromDate(date: LocalDate): Long {
        return date.toEpochDay()
    }
}