package com.landoulsi.catalog.ui.list

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.landoulsi.catalog.shared.presentation.common.ErrorMessage
import com.landoulsi.catalog.shared.presentation.products.ProductListUiState
import com.landoulsi.catalog.ui.previewProductUiState
import com.landoulsi.catalog.ui.theme.ProductCatalogTheme

@Preview(showBackground = true)
@Composable
private fun ProductListScreenLoadingPreview() {
    ProductCatalogTheme {
        ProductListScreen(
            uiState = ProductListUiState(isLoading = true),
            onQueryChange = {},
            onClearQuery = {},
            onProductClick = {},
            onToggleFavorite = {},
            onLoadMore = {},
            onRetry = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductListScreenListPreview() {
    ProductCatalogTheme {
        ProductListScreen(
            uiState = ProductListUiState(
                products = listOf(
                    previewProductUiState(id = 1, title = "Wireless Headphones", price = 79.99, isFavorite = false),
                    previewProductUiState(id = 2, title = "Mechanical Keyboard", price = 129.99, isFavorite = true),
                    previewProductUiState(id = 3, title = "USB-C Hub", price = 39.99, isFavorite = false),
                ),
            ),
            onQueryChange = {},
            onClearQuery = {},
            onProductClick = {},
            onToggleFavorite = {},
            onLoadMore = {},
            onRetry = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductListScreenEmptyPreview() {
    ProductCatalogTheme {
        ProductListScreen(
            uiState = ProductListUiState(query = "zzz"),
            onQueryChange = {},
            onClearQuery = {},
            onProductClick = {},
            onToggleFavorite = {},
            onLoadMore = {},
            onRetry = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductListScreenErrorPreview() {
    ProductCatalogTheme {
        ProductListScreen(
            uiState = ProductListUiState(error = ErrorMessage.Network),
            onQueryChange = {},
            onClearQuery = {},
            onProductClick = {},
            onToggleFavorite = {},
            onLoadMore = {},
            onRetry = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductsTopBarPreview() {
    ProductCatalogTheme {
        ProductsTopBar()
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductsSearchFieldEmptyPreview() {
    ProductCatalogTheme {
        ProductsSearchField(query = "", onQueryChange = {}, onClearQuery = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductsSearchFieldWithQueryPreview() {
    ProductCatalogTheme {
        ProductsSearchField(query = "phone", onQueryChange = {}, onClearQuery = {})
    }
}
