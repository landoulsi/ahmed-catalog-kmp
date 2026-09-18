package com.landoulsi.catalog.shared.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Envelope returned by both `/products` and `/products/search`. */
@Serializable
data class ProductListResponseDto(
    @SerialName("products")
    val products: List<ProductDto> = emptyList(),
    @SerialName("total")
    val total: Int = 0,
    @SerialName("skip")
    val skip: Int = 0,
    @SerialName("limit")
    val limit: Int = 0,
)
