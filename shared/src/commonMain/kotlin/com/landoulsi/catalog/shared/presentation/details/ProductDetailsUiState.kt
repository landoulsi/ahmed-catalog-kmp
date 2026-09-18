package com.landoulsi.catalog.shared.presentation.details

import com.landoulsi.catalog.shared.presentation.common.ErrorMessage
import com.landoulsi.catalog.shared.presentation.common.UiState

/**
 * The loaded product's details, flattened rather than wrapping the domain
 * [com.landoulsi.catalog.shared.domain.model.Product] (see [com.landoulsi.catalog.shared.presentation.common.ProductUiState]
 * for why).
 */
data class ProductDetailsContent(
    val title: String,
    val brand: String,
    val category: String,
    val description: String,
    val imageUrl: String,
    val formattedPrice: String,
    val hasDiscount: Boolean,
    val formattedDiscount: String,
    val formattedRating: String,
    val isInStock: Boolean,
    val stock: Int,
)

data class ProductDetailsUiState(
    val content: ProductDetailsContent? = null,
    val isFavorite: Boolean = false,
    val isLoading: Boolean = true,
    val error: ErrorMessage? = null,
) : UiState
