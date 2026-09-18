package com.landoulsi.catalog.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.landoulsi.catalog.shared.presentation.common.ProductUiState
import com.landoulsi.catalog.ui.theme.ProductCatalogTheme

private val previewProductUiState = ProductUiState(
    id = 1,
    title = "iPhone 9",
    category = "smartphones",
    thumbnail = "",
    formattedPrice = "549.00",
    formattedRating = "4.7",
    stock = 34,
    hasDiscount = true,
    formattedDiscount = "12",
    isFavorite = false,
)

@Preview(showBackground = true)
@Composable
private fun ProductRowNotInFavoritePreview() {
    ProductCatalogTheme {
        ProductRow(
            product = previewProductUiState,
            onClick = {},
            onToggleFavorite = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductRowInFavoritePreview() {
    ProductCatalogTheme {
        ProductRow(
            product = previewProductUiState.copy(isFavorite = true),
            onClick = {},
            onToggleFavorite = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductRowLongTitlePreview() {
    ProductCatalogTheme {
        ProductRow(
            product = previewProductUiState.copy(
                title = "A very long product title that should wrap onto a second line and then truncate",
            ),
            onClick = {},
            onToggleFavorite = {},
        )
    }
}
