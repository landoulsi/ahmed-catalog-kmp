package com.landoulsi.catalog.shared.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Product fields returned by DummyJSON's product read endpoints. */
@Serializable
data class ProductDto(
    @SerialName("id")
    val id: Int,
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String,
    @SerialName("price")
    val price: Double,
    @SerialName("discountPercentage")
    val discountPercentage: Double,
    @SerialName("rating")
    val rating: Double,
    @SerialName("stock")
    val stock: Int,
    @SerialName("brand")
    val brand: String? = null,
    @SerialName("category")
    val category: String,
    @SerialName("thumbnail")
    val thumbnail: String,
    @SerialName("images")
    val images: List<String>,
)
