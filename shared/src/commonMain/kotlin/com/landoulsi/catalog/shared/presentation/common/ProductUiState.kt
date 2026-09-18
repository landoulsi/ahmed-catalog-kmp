package com.landoulsi.catalog.shared.presentation.common

/**
 * UI state for an individual product item in a list.
 *
 * Flattened rather than wrapping the domain [com.landoulsi.catalog.shared.domain.model.Product]:
 * a UI layer that can read the raw model can also bypass its formatted fields
 * (e.g. reading [price] instead of [formattedPrice]), so the model stays out
 * of this type entirely and every field the row needs is exposed directly.
 */
data class ProductUiState(
    val id: Int,
    val title: String,
    val category: String,
    val thumbnail: String,
    val formattedPrice: String,
    val formattedRating: String,
    val stock: Int,
    val hasDiscount: Boolean,
    val formattedDiscount: String,
    val isFavorite: Boolean = false,
) : UiState {
    val isInStock: Boolean get() = stock > 0
}
