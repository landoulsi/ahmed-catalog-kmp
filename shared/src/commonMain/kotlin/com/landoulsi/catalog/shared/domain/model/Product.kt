package com.landoulsi.catalog.shared.domain.model

import kotlin.math.round

data class Product(
    val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val discountPercentage: Double,
    val rating: Double,
    val stock: Int,
    val brand: String,
    val category: String,
    val thumbnail: String,
    val images: List<String>,
) {
    val isInStock: Boolean get() = stock > 0

    /**
     * Price after [discountPercentage] is applied, rounded to the nearest cent.
     *
     * Rounds rather than truncates: an IEEE-754 product landing just below the
     * true value (1.01 at 1% gives 0.9999) would otherwise drop a cent.
     */
    val discountedPrice: Double
        get() = round(price * (100 - discountPercentage)) / 100.0
}
