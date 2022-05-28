package com.tommymarto.healthapp.ui

import android.graphics.PorterDuff
import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.tommymarto.healthapp.MainActivity
import com.tommymarto.healthapp.R
import com.tommymarto.healthapp.databinding.DayFragmentBinding
import com.tommymarto.healthapp.databinding.WeekFragmentBinding
import com.tommymarto.healthapp.utils.BarChartProperties
import com.tommymarto.healthapp.utils.DonutChartProperties
import com.tommymarto.healthapp.utils.fillBarChart
import com.tommymarto.healthapp.utils.fillDonutChart
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class DayFragment : Fragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private var _binding: DayFragmentBinding? = null
    private val binding get() = _binding!!

    private val persistentState = object {
        var selectedDay = LocalDateTime.now()
    }

    fun setState(day: LocalDateTime) {
        persistentState.selectedDay = day
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DayFragmentBinding.inflate(inflater, container, false)

//        when (persistentState.selectedDay.dayOfWeek) {
//            DayOfWeek.MONDAY -> binding.textViewMon.backgroundTintMode = PorterDuff.Mode.SRC_ATOP
//            DayOfWeek.TUESDAY -> binding.textViewTue.backgroundTintMode = PorterDuff.Mode.SRC_ATOP
//            DayOfWeek.WEDNESDAY -> binding.textViewWed.backgroundTintMode = PorterDuff.Mode.SRC_ATOP
//            DayOfWeek.THURSDAY -> binding.textViewThu.backgroundTintMode = PorterDuff.Mode.SRC_ATOP
//            DayOfWeek.FRIDAY -> binding.textViewFri.backgroundTintMode = PorterDuff.Mode.SRC_ATOP
//            DayOfWeek.SATURDAY -> binding.textViewSat.backgroundTintMode = PorterDuff.Mode.SRC_ATOP
//            DayOfWeek.SUNDAY -> binding.textViewSun.backgroundTintMode = PorterDuff.Mode.SRC_ATOP
//        }

        fillActivityDonutChart()
        fillMovementChart()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mapView.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_MapsFragment)
        }
    }

    override fun onResume() {
        super.onResume()

        val formattedDate = persistentState.selectedDay.format(DateTimeFormatter.ofPattern("dd LLLL yyyy"))
        val formattedDay = persistentState.selectedDay.format(DateTimeFormatter.ofPattern("E"))

        val isToday = DateUtils.isToday(ZonedDateTime.of(persistentState.selectedDay, ZoneId.systemDefault()).toInstant().toEpochMilli())
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
        val steps = 7538F
        val exerciseMinutes = 13F
        val standHours = 11F

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

    /**
     *  Bar chart stuff
     *
     *
     */
    private fun fillMovementChart() {
        val barCount = 48
        var labels = (0..barCount).map {
            when(it) {
                    barCount/4 -> "06:00"
                2 * barCount/4 -> "12:00"
                3 * barCount/4 -> "18:00"
                else -> ""
            }
        }
        val values = (0..barCount).map { it.toFloat() }
        val entries = labels zip values

        fillBarChart(
            binding.chartDayStepsDetails,
            entries,
            BarChartProperties(
                resources.getColor(R.color.brightDarkRed, activity?.theme)
            ),
            resources,
            activity
        )

        fillBarChart(
            binding.chartDayExerciseDetails,
            entries,
            BarChartProperties(
                resources.getColor(R.color.brightGreen, activity?.theme)
            ),
            resources,
            activity
        )

        fillBarChart(
            binding.chartDayStandDetails,
            entries,
            BarChartProperties(
                resources.getColor(R.color.brightCyan, activity?.theme)
            ),
            resources,
            activity
        )
    }
}