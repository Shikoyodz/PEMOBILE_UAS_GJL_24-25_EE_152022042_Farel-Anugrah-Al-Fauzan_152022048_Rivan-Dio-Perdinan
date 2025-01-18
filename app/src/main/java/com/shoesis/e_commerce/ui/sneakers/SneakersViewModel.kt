package com.shoesis.e_commerce.ui.sneakers

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
class SneakersViewModel @Inject constructor(
    private val ioDispatcher: CoroutineContext,
    private val productRepository: ProductRepository
) : ViewModel() {

    private var _sneakersList = MutableLiveData<List<ProductDto>>()
    val sneakersList: LiveData<List<ProductDto>>
        get() = _sneakersList

    init {
        getSneakersProduct()
    }

    private fun getSneakersProduct() {
        CoroutineScope(ioDispatcher).launch {
            productRepository.getSneakers(_sneakersList)
        }
    }
}