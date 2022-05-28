package com.tommymarto.healthapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.tommymarto.healthapp.databinding.ViewPagerHostFragmentBinding
import java.time.LocalDateTime

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class ViewPagerHostFragment : Fragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private var _binding: ViewPagerHostFragmentBinding? = null
    private val binding get() = _binding!!

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        _binding = ViewPagerHostFragmentBinding.inflate(inflater, container, false)

        viewPager = binding.pager
        // The pager adapter, which provides the pages to the view pager widget.
        val pagerAdapter = ScreenSlidePagerAdapter(this)
        viewPager.adapter = pagerAdapter

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
        _binding = null
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private inner class ScreenSlidePagerAdapter(fa: Fragment) : FragmentStateAdapter(fa) {
        val now = LocalDateTime.now()
        var _itemCount = 300
        override fun getItemCount(): Int = _itemCount

        override fun createFragment(position: Int): Fragment {
            val fragment = DayFragment()
            fragment.setState(now.minusDays(position.toLong()))

            return fragment
        }
    }
}