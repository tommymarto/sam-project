package com.tommymarto.healthapp.ui

import android.graphics.PorterDuff
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tommymarto.healthapp.R
import com.tommymarto.healthapp.databinding.DayFragmentBinding
import com.tommymarto.healthapp.databinding.WeekFragmentBinding
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
}