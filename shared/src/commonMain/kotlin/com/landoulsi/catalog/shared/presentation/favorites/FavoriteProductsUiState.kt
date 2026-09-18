package com.landoulsi.catalog.shared.presentation.favorites

import com.landoulsi.catalog.shared.presentation.common.ProductUiState
import com.landoulsi.catalog.shared.presentation.common.UiState

data class FavoriteProductsUiState(
    val favorites: List<ProductUiState> = emptyList(),
    val isLoading: Boolean = true,
) : UiState {
    val isEmpty: Boolean get() = favorites.isEmpty() && !isLoading
}
