package com.landoulsi.catalog.ui

import com.landoulsi.catalog.shared.domain.model.Product
import com.landoulsi.catalog.shared.presentation.common.ProductUiState

/** Minimal valid [Product] for previews; override only what a preview cares about. */
fun previewProduct(
    id: Int = 1,
    title: String = "Product $id",
    price: Double = 10.0,
    discountPercentage: Double = 0.0,
): Product = Product(
    id = id,
    title = title,
    description = "Description $id",
    price = price,
    discountPercentage = discountPercentage,
    rating = 4.5,
    stock = 5,
    brand = "Brand",
    category = "category",
    thumbnail = "",
    images = emptyList(),
)

fun previewProductUiState(
    id: Int = 1,
    title: String = "Product $id",
    price: Double = 10.0,
    category: String = "category",
    thumbnail: String = "",
    formattedPrice: String = "$price",
    formattedRating: String = "4.5",
    stock: Int = 5,
    hasDiscount: Boolean = false,
    formattedDiscount: String = "0",
    isFavorite: Boolean = false,
): ProductUiState = ProductUiState(
    id = id,
    title = title,
    category = category,
    thumbnail = thumbnail,
    formattedPrice = formattedPrice,
    formattedRating = formattedRating,
    stock = stock,
    hasDiscount = hasDiscount,
    formattedDiscount = formattedDiscount,
    isFavorite = isFavorite,
)
