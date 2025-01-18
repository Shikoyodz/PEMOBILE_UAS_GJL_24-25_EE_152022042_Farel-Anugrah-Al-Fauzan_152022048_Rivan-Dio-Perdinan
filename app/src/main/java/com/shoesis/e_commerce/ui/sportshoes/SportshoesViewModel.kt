package com.shoesis.e_commerce.ui.sportshoes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shoesis.e_commerce.data.dto.ProductDto
import com.shoesis.e_commerce.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class SportshoesViewModel @Inject constructor(
    private val ioDispatcher: CoroutineContext,
    private val productRepository: ProductRepository
) : ViewModel() {

    private var _sportshoesList = MutableLiveData<List<ProductDto>>()
    val sportshoesList: LiveData<List<ProductDto>>
        get() = _sportshoesList

    init {
        getSportshoesProducts()
    }

    private fun getSportshoesProducts() {
        CoroutineScope(ioDispatcher).launch {
            productRepository.getSportshoes(_sportshoesList)
        }
    }
}