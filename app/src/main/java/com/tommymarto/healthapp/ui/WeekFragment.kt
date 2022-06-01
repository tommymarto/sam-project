package com.tommymarto.healthapp.ui

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.db.williamchart.view.DonutChartView
import com.tommymarto.healthapp.R
import com.tommymarto.healthapp.databinding.WeekFragmentBinding
import com.tommymarto.healthapp.utils.DonutChartProperties
import com.tommymarto.healthapp.utils.fillDonutChart
import com.tommymarto.healthapp.utils.healthConnectManager
import com.tommymarto.healthapp.utils.weekOfYear
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDateTime

/**
 *      KNOWN ISSUE:
 *      When charts are used in a RecyclerView (and ViewPager2 uses a RecyclerView underneath),
 *          the chartConfiguration property might not be correctly initialized (the cause of the issue is still unknown)
 *      To mitigate this effect, charts are initialized with dummy data (zeroes) and then, asynchronously updated
 *          with real data coming from HealthConnect
 *      https://github.com/diogobernardino/williamchart/issues/283
 */

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

        initActivityDonutCharts()

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        updateSelectedDay()
        initActivityDonutCharts()
        fillActivityDonutCharts()
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

    /**
     *  Chart stuff
     */

    private data class ActivityDonut(
        val chartSteps: DonutChartView,
        val chartExercise: DonutChartView,
        val chartDistance: DonutChartView
    )

    private val days by lazy {
        listOf(
            ActivityDonut(
                binding.chartMonSteps,
                binding.chartMonExercise,
                binding.chartMonDistance
            ),
            ActivityDonut(
                binding.chartTueSteps,
                binding.chartTueExercise,
                binding.chartTueDistance
            ),
            ActivityDonut(
                binding.chartWedSteps,
                binding.chartWedExercise,
                binding.chartWedDistance
            ),
            ActivityDonut(
                binding.chartThuSteps,
                binding.chartThuExercise,
                binding.chartThuDistance
            ),
            ActivityDonut(
                binding.chartFriSteps,
                binding.chartFriExercise,
                binding.chartFriDistance
            ),
            ActivityDonut(
                binding.chartSatSteps,
                binding.chartSatExercise,
                binding.chartSatDistance
            ),
            ActivityDonut(
                binding.chartSunSteps,
                binding.chartSunExercise,
                binding.chartSunDistance
            )
        )
    }

    private fun initActivityDonutCharts() {
        days.forEach { fillDonut(it, 0F, 0F, 0F) }
    }

    private fun fillActivityDonutCharts() {
        val today = LocalDateTime.now()
        val first = if (selectedDay.weekOfYear == today.weekOfYear) today.dayOfWeek.ordinal else 6

        val dates = (selectedDay.dayOfWeek.ordinal downTo 0).map { selectedDay.minusDays(it.toLong()) } +
                    (1..(6 - selectedDay.dayOfWeek.ordinal)).map { selectedDay.plusDays(it.toLong()) }

        (first downTo 0).forEach {
            lifecycleScope.launch {
                healthConnectManager.generateDataIfNotPresent(dates[it])
                val dayActivity = healthConnectManager.getDayActivity(dates[it])
                fillDonut(
                    days[it],
                    dayActivity.steps.toFloat(),
                    dayActivity.activeTime,
                    dayActivity.distance.toFloat()
                )
            }
        }
    }

    private fun fillDonut(donut: ActivityDonut, steps: Float, exerciseMinutes: Float, distance: Float) {
        fillDonutChart(
            donut.chartSteps,
            steps,
            DonutChartProperties(
                resources.getColor(R.color.brightDarkRed, activity?.theme),
                resources.getColor(R.color.backgroundDarkRed, activity?.theme),
                10000F,
                18F
            )
        )

        fillDonutChart(
            donut.chartExercise,
            exerciseMinutes,
            DonutChartProperties(
                resources.getColor(R.color.brightGreen, activity?.theme),
                resources.getColor(R.color.backgroundGreen, activity?.theme),
                30F,
                18F
            )
        )

        fillDonutChart(
            donut.chartDistance,
            distance,
            DonutChartProperties(
                resources.getColor(R.color.brightCyan, activity?.theme),
                resources.getColor(R.color.backgroundCyan, activity?.theme),
                6000F,
                18F
            )
        )
    }
}