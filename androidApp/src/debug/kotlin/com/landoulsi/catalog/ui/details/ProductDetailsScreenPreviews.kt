package com.landoulsi.catalog.ui.details

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.landoulsi.catalog.shared.presentation.common.ErrorMessage
import com.landoulsi.catalog.shared.presentation.details.ProductDetailsContent
import com.landoulsi.catalog.shared.presentation.details.ProductDetailsUiState
import com.landoulsi.catalog.ui.theme.ProductCatalogTheme

private val previewProductDetailsContent = ProductDetailsContent(
    title = "Wireless Mouse",
    brand = "Brand",
    category = "category",
    description = "Description",
    imageUrl = "",
    formattedPrice = "22.49",
    hasDiscount = true,
    formattedDiscount = "10",
    formattedRating = "4.5",
    isInStock = true,
    stock = 5,
)

@Preview(showBackground = true)
@Composable
private fun ProductDetailsScreenLoadedPreview() {
    ProductCatalogTheme {
        ProductDetailsScreen(
            uiState = ProductDetailsUiState(
                content = previewProductDetailsContent,
                isFavorite = false,
                isLoading = false,
            ),
            onBack = {},
            onToggleFavorite = {},
            onRetry = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductDetailsScreenLoadingPreview() {
    ProductCatalogTheme {
        ProductDetailsScreen(
            uiState = ProductDetailsUiState(isLoading = true),
            onBack = {},
            onToggleFavorite = {},
            onRetry = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductDetailsScreenErrorPreview() {
    ProductCatalogTheme {
        ProductDetailsScreen(
            uiState = ProductDetailsUiState(
                content = null,
                isLoading = false,
                error = ErrorMessage.Server,
            ),
            onBack = {},
            onToggleFavorite = {},
            onRetry = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductDetailsTopBarNotInFavoritePreview() {
    ProductCatalogTheme {
        ProductDetailsTopBar(
            title = "iPhone 9",
            isFavorite = false,
            showFavoriteAction = true,
            onBack = {},
            onToggleFavorite = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductDetailsTopBarInFavoritePreview() {
    ProductCatalogTheme {
        ProductDetailsTopBar(
            title = "iPhone 9",
            isFavorite = true,
            showFavoriteAction = true,
            onBack = {},
            onToggleFavorite = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductDetailsTopBarLoadingPreview() {
    ProductCatalogTheme {
        ProductDetailsTopBar(
            title = null,
            isFavorite = false,
            showFavoriteAction = false,
            onBack = {},
            onToggleFavorite = {},
        )
    }
}
