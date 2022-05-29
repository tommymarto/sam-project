package com.tommymarto.healthapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.tommymarto.healthapp.databinding.ViewPagerHostFragmentBinding
import com.tommymarto.healthapp.utils.weekOfYear
import java.lang.ref.WeakReference
import java.time.LocalDateTime
import kotlin.math.ceil


const val TOTAL_DAYS = 301
const val TOTAL_WEEKS = (TOTAL_DAYS/7) + 1

class ViewPagerHostFragment : Fragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private var _binding: ViewPagerHostFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var weekViewPager: ViewPager2
    private lateinit var weekPagerAdapter: WeeklyViewPagerAdapter
    private lateinit var dayViewPager: ViewPager2
    private lateinit var dayPagerAdapter: DailyViewPagerAdapter
    private var onDayChangeCallback = object: ViewPager2.OnPageChangeCallback() {
        private var _lastPageSelected: DayFragment? = null
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)

            val dayFragment = dayPagerAdapter.getFragment(position)
            var weekFragment = weekPagerAdapter.getFragment(weekViewPager.currentItem)

            val lastPageDay = _lastPageSelected?.selectedDay ?: dayFragment.selectedDay


            val newWeekPos = weekViewPager.currentItem + (lastPageDay.weekOfYear - dayFragment.selectedDay.weekOfYear)
            if (newWeekPos != weekViewPager.currentItem) {
                weekViewPager.setCurrentItem(newWeekPos, true)
                weekFragment = weekPagerAdapter.getFragment(newWeekPos)
                weekFragment.selectedDay = dayFragment.selectedDay
            } else {
                weekFragment.selectedDay = dayFragment.selectedDay
                weekFragment.updateSelectedDay()
            }

            _lastPageSelected = dayFragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        _binding = ViewPagerHostFragmentBinding.inflate(inflater, container, false)

        // setup day pager
        dayViewPager = binding.dayPager
        dayPagerAdapter = DailyViewPagerAdapter(this)
        dayViewPager.adapter = dayPagerAdapter

        // setup week pager
        weekViewPager = binding.weekPager
        weekPagerAdapter = WeeklyViewPagerAdapter(this)
        weekViewPager.adapter = weekPagerAdapter
        weekViewPager.isUserInputEnabled = false

        dayViewPager.registerOnPageChangeCallback(onDayChangeCallback)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.buttonSecond.setOnClickListener {
//            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dayViewPager.unregisterOnPageChangeCallback(onDayChangeCallback)
        _binding = null
    }

    private inner class DailyViewPagerAdapter(fa: Fragment) : FragmentStateAdapter(fa) {
        val now = LocalDateTime.now()

        // only store as weak references, so you're not holding discarded fragments in memory
        private val fragmentCache = mutableMapOf<Int, WeakReference<DayFragment>>()

        override fun getItemCount(): Int = TOTAL_DAYS

        override fun createFragment(position: Int): Fragment {
            // return the cached fragment if there is one
            fragmentCache[position]?.get()?.let { return it }

            return DayFragment().also {
                it.selectedDay = now.minusDays(position.toLong())
                fragmentCache[position] = WeakReference(it)
            }
        }

        fun getFragment(position: Int): DayFragment = createFragment(position) as DayFragment
    }

    private inner class WeeklyViewPagerAdapter(fa: Fragment) : FragmentStateAdapter(fa) {
        // only store as weak references, so you're not holding discarded fragments in memory
        private val fragmentCache = mutableMapOf<Int, WeakReference<WeekFragment>>()

        override fun getItemCount(): Int = TOTAL_WEEKS
        override fun createFragment(position: Int): Fragment {
            // return the cached fragment if there is one
            fragmentCache[position]?.get()?.let { return it }

            return WeekFragment().also {
                fragmentCache[position] = WeakReference(it)
            }
        }

        fun getFragment(position: Int): WeekFragment = createFragment(position) as WeekFragment
    }
}