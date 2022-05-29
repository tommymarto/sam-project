package com.tommymarto.healthapp.utils

import android.app.Activity
import android.content.res.Resources
import androidx.annotation.ColorInt
import com.db.williamchart.data.AxisType
import com.db.williamchart.view.BarChartView
import com.db.williamchart.view.DonutChartView
import com.tommymarto.healthapp.R

/**
 *  Donut chart stuff
 *
 *
 */
data class DonutChartProperties(
    @ColorInt
    val color: Int,
    @ColorInt
    val background: Int,
    val max: Float,
    val thickness: Float = 70F
)

fun fillDonutChart(chart: DonutChartView, value: Float, properties: DonutChartProperties) {
    chart.donutColors = IntArray(1) { properties.color }
    chart.donutBackgroundColor = properties.background
    chart.donutTotal = properties.max
    chart.donutRoundCorners = true
    chart.donutThickness = properties.thickness
    chart.animate(listOf(value))
}

/**
 *  Bar chart stuff
 *
 *
 */
data class BarChartProperties(
    @ColorInt
    val color: Int
)

fun fillBarChart(chart: BarChartView, entries: List<Pair<String, Float>>, properties: BarChartProperties, resources: Resources, activity: Activity?) {
    chart.barsColor = properties.color
    chart.barRadius = 10F
    chart.axis = AxisType.X
    chart.labelsSize = 40F
    chart.labelsColor = resources.getColor(R.color.white, activity?.theme)

    chart.animate(entries)
}