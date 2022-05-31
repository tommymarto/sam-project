package com.tommymarto.healthapp.ui

import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.tommymarto.healthapp.MainActivity
import com.tommymarto.healthapp.R
import com.tommymarto.healthapp.databinding.DayFragmentBinding
import com.tommymarto.healthapp.utils.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class DayFragment : Fragment() {

    // This property is only valid between onCreateView and onDestroyView.
    private var _binding: DayFragmentBinding? = null
    private val binding get() = _binding!!

    var selectedDay: LocalDateTime = LocalDateTime.now()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DayFragmentBinding.inflate(inflater, container, false)

        fillActivityDonutChart()
        fillMovementChart()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mapView.setOnClickListener {
            findNavController().navigate(R.id.action_ViewPagerHostFragment_to_MapsFragment)
        }
    }

    override fun onResume() {
        super.onResume()

        val formattedDate = selectedDay.format(DateTimeFormatter.ofPattern("dd LLLL yyyy"))
        val formattedDay = selectedDay.format(DateTimeFormatter.ofPattern("E"))

        val isToday = DateUtils.isToday(ZonedDateTime.of(selectedDay, ZoneId.systemDefault()).toInstant().toEpochMilli())
        val day = if (isToday) "Today, $formattedDate" else "$formattedDay, $formattedDate"
        (activity as MainActivity).updateTitle(day)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    /**
     *  Donut chart stuff
     *
     *
     */
    private fun fillActivityDonutChart() {
        fun fill(steps: Float, exerciseMinutes: Float, standHours: Float) {
            fillDonutChart(
                binding.chartDaySteps,
                steps,
                DonutChartProperties(
                    resources.getColor(R.color.brightDarkRed, activity?.theme),
                    resources.getColor(R.color.backgroundDarkRed, activity?.theme),
                    10000F
                )
            )

            fillDonutChart(
                binding.chartDayExercise,
                exerciseMinutes,
                DonutChartProperties(
                    resources.getColor(R.color.brightGreen, activity?.theme),
                    resources.getColor(R.color.backgroundGreen, activity?.theme),
                    30F
                )
            )

            fillDonutChart(
                binding.chartDaySth,
                standHours,
                DonutChartProperties(
                    resources.getColor(R.color.brightCyan, activity?.theme),
                    resources.getColor(R.color.backgroundCyan, activity?.theme),
                    12F
                )
            )
        }

        fill(0F, 0F, 0F)

        lifecycleScope.launch {
            val dayActivity = healthConnectManager.getDayActivity(selectedDay)
            fill(
                dayActivity.steps.toFloat(),
                dayActivity.activeTime,
                dayActivity.distance.toFloat()
            )
        }
    }

    /**
     *  Bar chart stuff
     *
     *
     */
    private fun fillMovementChart() {
        val barCount = 48
        var labels = (0..barCount).map {
            when (it) {
                barCount / 4 -> "06:00"
                2 * barCount / 4 -> "12:00"
                3 * barCount / 4 -> "18:00"
                else -> ""
            }
        }

        fun fill(stepsDetails: List<Float>, exerciseDetails: List<Float>, distanceDetails: List<Float>) {
            fillBarChart(
                binding.chartDayStepsDetails,
                labels zip stepsDetails,
                BarChartProperties(
                    resources.getColor(R.color.brightDarkRed, activity?.theme)
                ),
                resources,
                activity
            )

            fillBarChart(
                binding.chartDayExerciseDetails,
                labels zip exerciseDetails,
                BarChartProperties(
                    resources.getColor(R.color.brightGreen, activity?.theme)
                ),
                resources,
                activity
            )

            fillBarChart(
                binding.chartDayStandDetails,
                labels zip distanceDetails,
                BarChartProperties(
                    resources.getColor(R.color.brightCyan, activity?.theme)
                ),
                resources,
                activity
            )
        }

        val dummyValues = (0..barCount).map { (it + 1).toFloat() }
        fill(dummyValues, dummyValues, dummyValues)

        lifecycleScope.launch {
            val dayActivityDetailed = healthConnectManager.getDayActivityDetailed(selectedDay)
//            fill(
//                dayActivity.steps.toFloat(),
//                dayActivity.activeTime,
//                dayActivity.distance.toFloat()
//            )
        }
    }
}