package com.shoesis.e_commerce.ui.workshoes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.shoesis.e_commerce.data.dto.ProductDto
import com.shoesis.e_commerce.databinding.FragmentWorkshoesBinding
import com.shoesis.e_commerce.ui.adapter.ProductAdapter
import com.shoesis.e_commerce.ui.favorites.FavoritesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WorkshoesFragment : Fragment() {

    private lateinit var rv: RecyclerView
    private lateinit var adapter: ProductAdapter
    private var _binding: FragmentWorkshoesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WorkshoesViewModel by viewModels()
    private val favoritesViewModel: FavoritesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkshoesBinding.inflate(inflater, container, false)
        observer()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observer() {
        viewModel.workshoesList.observe(viewLifecycleOwner) {
            adapter = ProductAdapter(it as MutableList<ProductDto>,requireContext(),layoutInflater,favoritesViewModel)
            rv = binding.workshoesRv
            rv.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            rv.adapter = adapter
        }
    }

}