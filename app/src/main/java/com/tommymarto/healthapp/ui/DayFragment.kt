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
import com.tommymarto.healthapp.databinding.FragmentFirstBinding
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class DayFragment : Fragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private var _binding: FragmentFirstBinding? = null
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

        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        when (persistentState.selectedDay.dayOfWeek) {
            DayOfWeek.MONDAY -> binding.textViewMon.backgroundTintMode = PorterDuff.Mode.SRC_ATOP
            DayOfWeek.TUESDAY -> binding.textViewTue.backgroundTintMode = PorterDuff.Mode.SRC_ATOP
            DayOfWeek.WEDNESDAY -> binding.textViewWed.backgroundTintMode = PorterDuff.Mode.SRC_ATOP
            DayOfWeek.THURSDAY -> binding.textViewThu.backgroundTintMode = PorterDuff.Mode.SRC_ATOP
            DayOfWeek.FRIDAY -> binding.textViewFri.backgroundTintMode = PorterDuff.Mode.SRC_ATOP
            DayOfWeek.SATURDAY -> binding.textViewSat.backgroundTintMode = PorterDuff.Mode.SRC_ATOP
            DayOfWeek.SUNDAY -> binding.textViewSun.backgroundTintMode = PorterDuff.Mode.SRC_ATOP
        }

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
}