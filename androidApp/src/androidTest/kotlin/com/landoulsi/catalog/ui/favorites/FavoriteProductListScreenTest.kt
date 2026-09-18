package com.landoulsi.catalog.ui.favorites

import android.content.Context
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.landoulsi.catalog.R
import com.landoulsi.catalog.shared.presentation.favorites.FavoriteProductsUiState
import com.landoulsi.catalog.ui.testProductUiState
import com.landoulsi.catalog.ui.theme.ProductCatalogTheme
import org.junit.Rule
import org.junit.Test

/** Drives the stateless [FavoriteProductsScreen] with plain state and recorded callbacks. */
class FavoriteProductListScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context = ApplicationProvider.getApplicationContext<Context>()

    private fun setContent(
        uiState: FavoriteProductsUiState,
        onProductClick: (Int) -> Unit = {},
        onRemoveFavorite: (Int) -> Unit = {},
    ) {
        composeRule.setContent {
            ProductCatalogTheme {
                FavoriteProductsScreen(
                    uiState = uiState,
                    onProductClick = onProductClick,
                    onRemoveFavorite = onRemoveFavorite,
                )
            }
        }
    }

    @Test
    fun showsEmptyStateWhenNothingIsSaved() {
        setContent(uiState = FavoriteProductsUiState(favorites = emptyList(), isLoading = false))

        composeRule.onNodeWithText(
            context.getString(R.string.favorites_empty)
        ).assertExists()
    }

    @Test
    fun showsEachSavedProduct() {
        setContent(
            uiState = FavoriteProductsUiState(
                favorites = listOf(
                    testProductUiState(id = 1, title = HEADPHONES_TITLE, isFavorite = true),
                    testProductUiState(id = 2, title = KEYBOARD_TITLE, isFavorite = true),
                ),
                isLoading = false,
            ),
        )

        composeRule.onNodeWithText(HEADPHONES_TITLE).assertExists()
        composeRule.onNodeWithText(KEYBOARD_TITLE).assertExists()
    }

    @Test
    fun everyRowShowsTheRemoveDescriptionSinceAllAreFavorites() {
        setContent(
            uiState = FavoriteProductsUiState(
                favorites = listOf(
                    testProductUiState(id = 1, isFavorite = true),
                    testProductUiState(id = 2, isFavorite = true),
                ),
                isLoading = false,
            ),
        )

        val removeDescription = context.getString(R.string.favorite_remove)
        composeRule.onAllNodesWithContentDescription(removeDescription).assertCountEquals(2)
    }

    @Test
    fun tappingRemoveInvokesCallbackWithThatProductId() {
        var removedId: Int? = null
        val product = testProductUiState(id = 5, title = MONITOR_TITLE, isFavorite = true)
        setContent(
            uiState = FavoriteProductsUiState(favorites = listOf(product), isLoading = false),
            onRemoveFavorite = { removedId = it },
        )

        val removeDescription = context.getString(R.string.favorite_remove)
        composeRule.onNodeWithContentDescription(removeDescription).performClick()

        assert(removedId == 5) { "expected onRemoveFavorite(5), got $removedId" }
    }

    @Test
    fun tappingAFavoriteRowReportsItsId() {
        var clickedId: Int? = null
        setContent(
            uiState = FavoriteProductsUiState(
                favorites = listOf(testProductUiState(id = 9, title = SPEAKER_TITLE, isFavorite = true)),
                isLoading = false,
            ),
            onProductClick = { clickedId = it },
        )

        composeRule.onNodeWithText(SPEAKER_TITLE).performClick()

        assert(clickedId == 9) { "expected onProductClick(9), got $clickedId" }
    }

    private companion object {
        const val HEADPHONES_TITLE = "Headphones"
        const val KEYBOARD_TITLE = "Keyboard"
        const val MONITOR_TITLE = "Monitor"
        const val SPEAKER_TITLE = "Speaker"
    }
}
