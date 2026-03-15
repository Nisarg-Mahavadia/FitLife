package com.marwadiuniversity.fitlife

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class HomePagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 2  // Your Analysis + Daily Tasks

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AnalysisFragment()
            else -> DailyTasksFragment()
        }
    }
}
