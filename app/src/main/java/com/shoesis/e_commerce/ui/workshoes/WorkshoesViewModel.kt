package com.shoesis.e_commerce.ui.workshoes

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
class WorkshoesViewModel @Inject constructor(
    private val ioDispatcher: CoroutineContext,
    private val productRepository: ProductRepository
) : ViewModel() {

    private var _workshoesList = MutableLiveData<List<ProductDto>>()
    val workshoesList: LiveData<List<ProductDto>>
        get() = _workshoesList

    init {
        getWorkshoesProducts()
    }

    private fun getWorkshoesProducts() {
        CoroutineScope(ioDispatcher).launch {
            productRepository.getWorkshoes(_workshoesList)
        }
    }
}