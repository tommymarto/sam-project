package com.tommymarto.healthapp.utils

import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.math.floor

val LocalDateTime.weekOfYear: Int
    get() {
        val daysWithinWeekFromYearFirstDay = ((this.dayOfYear - 1) % 7)
        val weekDayFromNewYear = LocalDate.of(this.year, 1, daysWithinWeekFromYearFirstDay + 1)
        val firstDayOfYear = LocalDate.of(this.year, 1, 1)

        return floor((this.dayOfYear - 1) / 7F).toInt().let {
            it + 1 + if (weekDayFromNewYear.dayOfWeek.ordinal < firstDayOfYear.dayOfWeek.ordinal) 1 else 0
        }
    }