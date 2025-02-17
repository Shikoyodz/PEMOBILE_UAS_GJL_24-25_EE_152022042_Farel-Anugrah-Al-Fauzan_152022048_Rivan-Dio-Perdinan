package com.shoesis.e_commerce.data.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BrandDto(
    @SerializedName("id")
    @Expose
    val id: Int,

    @SerializedName("name")
    @Expose
    val name: String,

    @SerializedName("picUrl")
    @Expose
    val picUrl: String,
)