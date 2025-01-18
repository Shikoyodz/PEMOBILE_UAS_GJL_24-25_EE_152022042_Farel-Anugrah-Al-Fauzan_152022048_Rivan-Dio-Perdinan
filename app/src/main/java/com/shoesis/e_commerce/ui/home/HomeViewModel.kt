package com.shoesis.e_commerce.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shoesis.e_commerce.data.dto.BrandDto
import com.shoesis.e_commerce.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val ioDispatcher: CoroutineContext
) : ViewModel() {

    private var _brandDtoList = MutableLiveData<List<BrandDto>>()
    val brandDtoList: LiveData<List<BrandDto>>
        get() = _brandDtoList


}