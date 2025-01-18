package com.shoesis.e_commerce.domain.model.response

import com.google.gson.annotations.SerializedName
import com.shoesis.e_commerce.data.dto.BrandDto

data class BrandResponse(
    @SerializedName("success")
    val success: Int = 0,
    @SerializedName("brands")
    val brandDtoResponse: List<BrandDto>?
    )