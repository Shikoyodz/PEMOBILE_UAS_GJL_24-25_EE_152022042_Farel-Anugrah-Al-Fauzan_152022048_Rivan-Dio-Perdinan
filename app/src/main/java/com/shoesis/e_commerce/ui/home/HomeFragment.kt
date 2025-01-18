package com.shoesis.e_commerce.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.material.tabs.TabLayoutMediator
import com.shoesis.e_commerce.databinding.FragmentHomeBinding
import com.shoesis.e_commerce.ui.adapter.BrandAdapter
import com.shoesis.e_commerce.ui.home.viewpager.ViewPagerAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var rv: RecyclerView
    private lateinit var adapter: BrandAdapter
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this){
            requireActivity().finish()
        }

        observer()

        val adUrlList = ArrayList<SlideModel>()
        val tabLayoutList = arrayListOf(
            "Newest🔥",
            "Sport Shoes",
            "Sneakers",
            "Sandals",
            "Boots",
            "Work Shoes"
        )

        binding.productViewPager.adapter = ViewPagerAdapter(parentFragmentManager, lifecycle)
        binding.productViewPager.offscreenPageLimit = 100

        TabLayoutMediator(
            binding.homeTabLayout,
            binding.productViewPager,
            true,
            true
        ) { tab, position ->
            tab.text = tabLayoutList[position]
        }.attach()

        adUrlList.add(SlideModel("https://i.ibb.co.com/NLGQ3L3/1.png"))
        adUrlList.add(SlideModel("https://i.ibb.co.com/4JNNfz0/2.png"))
        adUrlList.add(SlideModel("https://i.ibb.co.com/N91gsZB/3.png"))
        adUrlList.add(SlideModel("https://i.ibb.co.com/7gGsw14/4.png"))
        adUrlList.add(SlideModel("https://i.ibb.co.com/ftPnHW4/5.png"))


        binding.adImageSlider.setImageList(adUrlList)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observer() {
        homeViewModel.brandDtoList.observe(viewLifecycleOwner) {
            adapter = BrandAdapter(it)
            rv = binding.brandRecyclerView
            rv.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            rv.adapter = adapter
        }
    }
}