package com.landoulsi.catalog.shared.presentation.products

import com.landoulsi.catalog.shared.presentation.common.ErrorMessage
import com.landoulsi.catalog.shared.presentation.common.ProductUiState
import com.landoulsi.catalog.shared.presentation.common.UiState

/**
 * Everything the products list screen renders, as one immutable value.
 *
 * A single state object (rather than several independent flows) means the UI
 * can never observe a half-applied update.
 */
data class ProductListUiState(
    val query: String = "",
    val products: List<ProductUiState> = emptyList(),
    /** First load, or a reload after the query changed. */
    val isLoading: Boolean = false,
    /** A further page is being appended to [products]. */
    val isLoadingMore: Boolean = false,
    val canLoadMore: Boolean = false,
    val error: ErrorMessage? = null,
) : UiState {
    /** True when a finished load produced nothing at all. */
    val isEmpty: Boolean get() = products.isEmpty() && !isLoading && error == null
}
