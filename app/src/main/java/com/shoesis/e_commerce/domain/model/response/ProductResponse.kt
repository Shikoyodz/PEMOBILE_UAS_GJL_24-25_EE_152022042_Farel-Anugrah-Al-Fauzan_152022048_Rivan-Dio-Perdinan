package com.shoesis.e_commerce.domain.model.response

import com.shoesis.e_commerce.data.dto.ProductDto

// Data class untuk membungkus produk dari Firestore
data class ProductResponse(
    val success: Int = 1, // Menandakan apakah operasi berhasil (nilai default 1 berarti berhasil)
    val productDtoResponse: List<ProductDto>? = null // Daftar produk yang diambil dari Firestore
)
