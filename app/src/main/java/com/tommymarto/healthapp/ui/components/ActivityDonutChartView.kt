package com.tommymarto.healthapp.ui.components

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.tommymarto.healthapp.R
import com.tommymarto.healthapp.databinding.ActivityDonutChartViewBinding
import com.tommymarto.healthapp.utils.DonutChartProperties
import com.tommymarto.healthapp.utils.fillDonutChart

class ActivityDonutChartView(context: Context, attributeSet: AttributeSet?): ConstraintLayout(context, attributeSet) {

    // This property is only valid between onCreateView and onDestroyView.
    private var _binding: ActivityDonutChartViewBinding? = null
    private val binding get() = _binding!!

    init {
        inflate(context, R.layout.activity_donut_chart_view, this)


        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        _binding = ActivityDonutChartViewBinding.inflate(inflater)

        fill()
    }

    private fun fill() {
//        val steps = 7832F
//        val exerciseMinutes = 18F
//        val standHours = 11F
//
//        fillDonutChart(
//            binding.chartDaySteps,
//            steps,
//            DonutChartProperties(
//                resources.getColor(R.color.brightDarkRed, null),
//                resources.getColor(R.color.backgroundDarkRed, null),
//                10000F,
//                18F
//            )
//        )
//
//        fillDonutChart(
//            binding.chartDayExercise,
//            exerciseMinutes,
//            DonutChartProperties(
//                resources.getColor(R.color.brightGreen, null),
//                resources.getColor(R.color.backgroundGreen, null),
//                30F,
//                18F
//            )
//        )
//
//        fillDonutChart(
//            binding.chartDaySth,
//            standHours,
//            DonutChartProperties(
//                resources.getColor(R.color.brightCyan, null),
//                resources.getColor(R.color.backgroundCyan, null),
//                12F,
//                18F
//            )
//        )
    }
}