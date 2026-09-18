package com.landoulsi.catalog.ui.favorites

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.landoulsi.catalog.shared.presentation.favorites.FavoriteProductsUiState
import com.landoulsi.catalog.ui.previewProductUiState
import com.landoulsi.catalog.ui.theme.ProductCatalogTheme

@Preview(showBackground = true)
@Composable
private fun FavoriteProductsScreenEmptyPreview() {
    ProductCatalogTheme {
        FavoriteProductsScreen(
            uiState = FavoriteProductsUiState(favorites = emptyList(), isLoading = false),
            onProductClick = {},
            onRemoveFavorite = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FavoriteProductsScreenListPreview() {
    ProductCatalogTheme {
        FavoriteProductsScreen(
            uiState = FavoriteProductsUiState(
                favorites = listOf(
                    previewProductUiState(id = 1, title = "Wireless Headphones", price = 79.99, isFavorite = true),
                    previewProductUiState(id = 2, title = "Mechanical Keyboard", price = 129.99, isFavorite = true),
                ),
                isLoading = false,
            ),
            onProductClick = {},
            onRemoveFavorite = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FavoriteProductsTopBarPreview() {
    ProductCatalogTheme {
        FavoriteProductsTopBar()
    }
}
