package com.shoesis.e_commerce.ui.home.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.shoesis.e_commerce.ui.boots.BootsFragment
import com.shoesis.e_commerce.ui.sandals.SandalsFragment
import com.shoesis.e_commerce.ui.workshoes.WorkshoesFragment
import com.shoesis.e_commerce.ui.sneakers.SneakersFragment
import com.shoesis.e_commerce.ui.newest.NewestFragment
import com.shoesis.e_commerce.ui.sportshoes.SportshoesFragment

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: androidx.lifecycle.Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    // List of fragments
    private val fragments = listOf(
        NewestFragment(),
        SportshoesFragment(),
        SneakersFragment(),
        SandalsFragment(),
        BootsFragment(),
        WorkshoesFragment(),
    )

    override fun getItemCount(): Int {
        // Jumlah fragment yang ditampilkan
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        // Mengembalikan fragment yang sesuai dengan posisi
        return fragments[position]
    }
}
