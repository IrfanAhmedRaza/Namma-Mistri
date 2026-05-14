package com.nammamistri.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.nammamistri.ui.calculator.CalculatorFragment
import com.nammamistri.ui.sites.SitesFragment
import com.nammamistri.ui.workers.WorkersFragment

class MainPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount() = 3
    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> CalculatorFragment()
        1 -> SitesFragment()
        2 -> WorkersFragment()
        else -> CalculatorFragment()
    }
}
