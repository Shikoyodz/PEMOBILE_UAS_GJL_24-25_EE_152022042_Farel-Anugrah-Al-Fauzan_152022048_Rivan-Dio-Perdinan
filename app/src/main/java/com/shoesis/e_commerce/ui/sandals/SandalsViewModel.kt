package com.shoesis.e_commerce.ui.sandals

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
class SandalsViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val ioDispatcher: CoroutineContext
) : ViewModel() {

    private val _sandalsList = MutableLiveData<List<ProductDto>>()
    val sandalsList: MutableLiveData<List<ProductDto>>
        get() = _sandalsList

    init {
        getSandalsProducts()
    }

    private fun getSandalsProducts() {
        CoroutineScope(ioDispatcher).launch {
            productRepository.getSandals(_sandalsList)
        }
    }
}