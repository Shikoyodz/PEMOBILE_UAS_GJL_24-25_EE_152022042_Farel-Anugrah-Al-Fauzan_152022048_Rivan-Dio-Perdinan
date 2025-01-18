package com.shoesis.e_commerce.ui.boots

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
class BootsViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val ioDispatcher: CoroutineContext
) : ViewModel() {

    private val _bootsList = MutableLiveData<List<ProductDto>>()
    val bootsList: LiveData<List<ProductDto>>
        get() = _bootsList

    init {
        getBootsProducts()
    }

    private fun getBootsProducts() {
        CoroutineScope(ioDispatcher).launch {
            productRepository.getBoots(_bootsList)
        }
    }
}