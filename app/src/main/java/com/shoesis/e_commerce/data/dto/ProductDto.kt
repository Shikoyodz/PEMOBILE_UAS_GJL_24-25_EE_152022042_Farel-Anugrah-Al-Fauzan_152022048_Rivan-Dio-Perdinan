package com.shoesis.e_commerce.data.dto

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductDto(

    @SerializedName("id")
    @Expose
    var id: Int = 0,

    @SerializedName("name")
    @Expose
    val productName: String = "",

    @SerializedName("price")
    @Expose
    val price: Int = 0,

    @SerializedName("brand")
    @Expose
    val brand: String = "",

    @SerializedName("picUrl")
    @Expose
    val picUrl: String = "",

    @SerializedName("category")
    @Expose
    val category: String = "",

    @SerializedName("description")
    @Expose
    val description: String = "",

    var isFavorite: Boolean? = false,

    @SerializedName("rate")
    @Expose
    var rate: Int = 0,

    @SerializedName("stock")
    @Expose
    var stock: Int = 0,

    var piece: Int = 1,

    ):Parcelable