package com.tommymarto.healthapp.ui

import android.graphics.PorterDuff
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.db.williamchart.view.DonutChartView
import com.tommymarto.healthapp.R
import com.tommymarto.healthapp.databinding.DayFragmentBinding
import com.tommymarto.healthapp.databinding.WeekFragmentBinding
import com.tommymarto.healthapp.utils.DonutChartProperties
import com.tommymarto.healthapp.utils.fillDonutChart
import com.tommymarto.healthapp.utils.healthConnectManager
import java.time.DayOfWeek
import java.time.LocalDateTime

class WeekFragment : Fragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private var _binding: WeekFragmentBinding? = null
    private val binding get() = _binding!!

    var selectedDay: LocalDateTime = LocalDateTime.now()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = WeekFragmentBinding.inflate(inflater, container, false)

        fillActivityDonutCharts()
        updateSelectedDay()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun updateSelectedDay() {
        val days = listOf(
            binding.textViewMon,
            binding.textViewTue,
            binding.textViewWed,
            binding.textViewThu,
            binding.textViewFri,
            binding.textViewSat,
            binding.textViewSun
        )

        days.forEach {
            it.backgroundTintMode = PorterDuff.Mode.MULTIPLY
        }


        when (selectedDay.dayOfWeek) {
            DayOfWeek.MONDAY -> binding.textViewMon.backgroundTintMode = PorterDuff.Mode.SRC_ATOP
            DayOfWeek.TUESDAY -> binding.textViewTue.backgroundTintMode = PorterDuff.Mode.SRC_ATOP
            DayOfWeek.WEDNESDAY -> binding.textViewWed.backgroundTintMode = PorterDuff.Mode.SRC_ATOP
            DayOfWeek.THURSDAY -> binding.textViewThu.backgroundTintMode = PorterDuff.Mode.SRC_ATOP
            DayOfWeek.FRIDAY -> binding.textViewFri.backgroundTintMode = PorterDuff.Mode.SRC_ATOP
            DayOfWeek.SATURDAY -> binding.textViewSat.backgroundTintMode = PorterDuff.Mode.SRC_ATOP
            DayOfWeek.SUNDAY -> binding.textViewSun.backgroundTintMode = PorterDuff.Mode.SRC_ATOP
            else -> {}
        }
    }

    private fun fillActivityDonutCharts() {
        data class ActivityDonut(
            val chartSteps: DonutChartView,
            val chartExercise: DonutChartView,
            val chartSth: DonutChartView
        )

        val days = listOf(
            ActivityDonut(
                binding.chartMonSteps,
                binding.chartMonExercise,
                binding.chartMonSth
            ),
            ActivityDonut(
                binding.chartTueSteps,
                binding.chartTueExercise,
                binding.chartTueSth
            ),
            ActivityDonut(
                binding.chartWedSteps,
                binding.chartWedExercise,
                binding.chartWedSth
            ),
            ActivityDonut(
                binding.chartThuSteps,
                binding.chartThuExercise,
                binding.chartThuSth
            ),
            ActivityDonut(
                binding.chartFriSteps,
                binding.chartFriExercise,
                binding.chartFriSth
            ),
            ActivityDonut(
                binding.chartSatSteps,
                binding.chartSatExercise,
                binding.chartSatSth
            ),
            ActivityDonut(
                binding.chartSunSteps,
                binding.chartSunExercise,
                binding.chartSunSth
            )
        )

        days.forEach {
            val steps = 7832F
            val exerciseMinutes = 18F
            val standHours = 11F

            fillDonutChart(
                it.chartSteps,
                steps,
                DonutChartProperties(
                    resources.getColor(R.color.brightDarkRed, activity?.theme),
                    resources.getColor(R.color.backgroundDarkRed, activity?.theme),
                    10000F,
                    18F
                )
            )

            fillDonutChart(
                it.chartExercise,
                exerciseMinutes,
                DonutChartProperties(
                    resources.getColor(R.color.brightGreen, activity?.theme),
                    resources.getColor(R.color.backgroundGreen, activity?.theme),
                    30F,
                    18F
                )
            )

            fillDonutChart(
                it.chartSth,
                standHours,
                DonutChartProperties(
                    resources.getColor(R.color.brightCyan, activity?.theme),
                    resources.getColor(R.color.backgroundCyan, activity?.theme),
                    12F,
                    18F
                )
            )
        }


    }
}