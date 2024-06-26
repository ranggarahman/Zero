package com.example.zero.ui.achievement

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.example.zero.R
import com.example.zero.databinding.FragmentAchievementBinding
import com.example.zero.ui.achievement.badges.BadgesFragment
import com.example.zero.ui.achievement.leaderboard.LeaderboardFragment
import com.example.zero.ui.dashboard.DashboardFragment

class AchievementFragment : Fragment() {

    private var _binding: FragmentAchievementBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val achievementViewModel =
            ViewModelProvider(this)[AchievementViewModel::class.java]

        _binding = FragmentAchievementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewPager(binding.tabViewpager)
        binding.tabTablayout.setupWithViewPager(binding.tabViewpager)

        val sharedPreferences = requireContext().getSharedPreferences(
            ACHIEVEMENT_PREFS_NAME_FIRST_TIME_TUTORIAL, Context.MODE_PRIVATE)
        val isFirstTime = sharedPreferences.getBoolean(ACHIEVEMENT_KEY_FIRST_TIME_TUTORIAL, true)

        // Add a page change listener to handle showing the prompt for BadgesFragment
        binding.tabViewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                if (isFirstTime && position == 1 ) {
                    val badgesFragment = childFragmentManager.findFragmentByTag(
                        "android:switcher:" + R.id.tab_viewpager + ":1"
                    ) as? BadgesFragment
                    //badgesFragment?.showRecyclerViewItemPrompt()

                    with(sharedPreferences.edit()) {
                        putBoolean(ACHIEVEMENT_KEY_FIRST_TIME_TUTORIAL, false)
                        apply()
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

    }

    private fun setupViewPager(viewpager: ViewPager) {
        val adapter = ViewPagerAdapter(childFragmentManager)

        adapter.addFragment(LeaderboardFragment(), "Papan Peringkat")
        adapter.addFragment(BadgesFragment(), "Badges")

        // setting adapter to view pager.
        viewpager.adapter = adapter
    }

    // This "ViewPagerAdapter" class overrides functions which are
    // necessary to get information about which item is selected
    // by user, what is title for selected item and so on.*/
    class ViewPagerAdapter// this is a secondary constructor of ViewPagerAdapter class.
        (supportFragmentManager: FragmentManager) : FragmentPagerAdapter(supportFragmentManager) {

        // objects of arraylist. One is of Fragment type and
        // another one is of String type.*/
        private var fragmentList1: ArrayList<Fragment> = ArrayList()
        private var fragmentTitleList1: ArrayList<String> = ArrayList()

        // returns which item is selected from arraylist of fragments.
        override fun getItem(position: Int): Fragment {
            return fragmentList1[position]
        }

        // returns which item is selected from arraylist of titles.
        override fun getPageTitle(position: Int): CharSequence {
            return fragmentTitleList1[position]
        }

        // returns the number of items present in arraylist.
        override fun getCount(): Int {
            return fragmentList1.size
        }

        // this function adds the fragment and title in 2 separate  arraylist.
        fun addFragment(fragment: Fragment, title: String) {
            fragmentList1.add(fragment)
            fragmentTitleList1.add(title)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ACHIEVEMENT_PREFS_NAME_FIRST_TIME_TUTORIAL = "ACHIEVEMENT_PREFS_NAME_FIRST_TIME_TUTORIAL"
        const val ACHIEVEMENT_KEY_FIRST_TIME_TUTORIAL = "ACHIEVEMENT_KEY_FIRST_TIME_TUTORIAL"
    }
}