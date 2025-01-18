package com.shoesis.e_commerce.ui.sandals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.shoesis.e_commerce.data.dto.ProductDto
import com.shoesis.e_commerce.databinding.FragmentSandalsBinding
import com.shoesis.e_commerce.ui.adapter.ProductAdapter
import com.shoesis.e_commerce.ui.favorites.FavoritesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SandalsFragment : Fragment() {

    private val viewModel: SandalsViewModel by viewModels()
    private lateinit var rv: RecyclerView
    private lateinit var adapter: ProductAdapter
    private var _binding: FragmentSandalsBinding? = null
    private val binding get() = _binding!!
    private val favoritesViewModel: FavoritesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSandalsBinding.inflate(inflater, container, false)
        observer()
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observer() {
        viewModel.sandalsList.observe(viewLifecycleOwner) {
            adapter = ProductAdapter(it as MutableList<ProductDto>,requireContext(),layoutInflater,favoritesViewModel)
            rv = binding.sandalsRv
            rv.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            rv.adapter = adapter
        }
    }
}