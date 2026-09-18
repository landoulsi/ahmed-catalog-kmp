package com.landoulsi.catalog.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.landoulsi.catalog.ui.theme.ProductCatalogTheme

@Preview(showBackground = true)
@Composable
private fun LoadingStatePreview() {
    ProductCatalogTheme {
        LoadingState()
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ErrorStatePreview() {
    ProductCatalogTheme {
        ErrorState(message = "Couldn't load products. Check your connection.", onRetry = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyStatePreview() {
    ProductCatalogTheme {
        EmptyState(title = "No products found")
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyStateWithSubtitlePreview() {
    ProductCatalogTheme {
        EmptyState(
            title = "No favorites yet",
            subtitle = "Tap the bookmark icon on a product to save it here.",
        )
    }
}
