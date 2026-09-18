package com.landoulsi.catalog.ui.details

import android.content.Context
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.landoulsi.catalog.R
import com.landoulsi.catalog.shared.presentation.common.ErrorMessage
import com.landoulsi.catalog.shared.presentation.details.ProductDetailsContent
import com.landoulsi.catalog.shared.presentation.details.ProductDetailsUiState
import com.landoulsi.catalog.ui.theme.ProductCatalogTheme
import org.junit.Rule
import org.junit.Test

/** Drives the stateless [ProductDetailsScreen] with plain state and recorded callbacks. */
class ProductDetailsScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context = ApplicationProvider.getApplicationContext<Context>()

    private fun testContent(
        title: String = "Wireless Mouse",
        description: String = "Description",
    ) = ProductDetailsContent(
        title = title,
        brand = "Brand",
        category = "category",
        description = description,
        imageUrl = "",
        formattedPrice = "10.00",
        hasDiscount = false,
        formattedDiscount = "0",
        formattedRating = "4.5",
        isInStock = true,
        stock = 5,
    )

    private fun setContent(
        uiState: ProductDetailsUiState,
        onBack: () -> Unit = {},
        onToggleFavorite: () -> Unit = {},
        onRetry: () -> Unit = {},
    ) {
        composeRule.setContent {
            ProductCatalogTheme {
                ProductDetailsScreen(
                    uiState = uiState,
                    onBack = onBack,
                    onToggleFavorite = onToggleFavorite,
                    onRetry = onRetry,
                )
            }
        }
    }

    @Test
    fun showsTheLoadedProductsTitleAndDescription() {
        setContent(
            uiState = ProductDetailsUiState(
                content = testContent(title = "Wireless Mouse", description = "A description"),
                isLoading = false,
            ),
        )

        composeRule.onAllNodesWithText("Wireless Mouse").assertCountEquals(2)
        composeRule.onNodeWithText("A description").assertExists()
    }

    @Test
    fun favoriteIconIsHiddenUntilTheProductHasLoaded() {
        setContent(uiState = ProductDetailsUiState(content = null, isLoading = true))

        val addDescription = context.getString(R.string.favorite_add)
        composeRule.onNodeWithContentDescription(addDescription).assertDoesNotExist()
    }

    @Test
    fun tappingFavoriteIconInvokesCallback() {
        var toggled = false
        setContent(
            uiState = ProductDetailsUiState(
                content = testContent(),
                isFavorite = false,
                isLoading = false,
            ),
            onToggleFavorite = { toggled = true },
        )

        val addDescription = context.getString(R.string.favorite_add)
        composeRule.onNodeWithContentDescription(addDescription).performClick()

        assert(toggled) { "expected onToggleFavorite() to be invoked" }
    }

    @Test
    fun inFavoriteProductShowsRemoveDescription() {
        setContent(
            uiState = ProductDetailsUiState(
                content = testContent(),
                isFavorite = true,
                isLoading = false,
            ),
        )

        val removeDescription = context.getString(R.string.favorite_remove)
        composeRule.onNodeWithContentDescription(removeDescription).assertExists()
    }

    @Test
    fun errorStateShowsRetryButtonThatInvokesCallback() {
        var retried = false
        setContent(
            uiState = ProductDetailsUiState(
                content = null,
                isLoading = false,
                error = ErrorMessage.Server,
            ),
            onRetry = { retried = true },
        )

        val retryLabel = context.getString(R.string.action_retry)
        composeRule.onNodeWithText(retryLabel).performClick()

        assert(retried) { "expected onRetry() to be invoked" }
    }

    @Test
    fun tappingBackInvokesCallback() {
        var wentBack = false
        setContent(
            uiState = ProductDetailsUiState(content = testContent(), isLoading = false),
            onBack = { wentBack = true },
        )

        val backLabel = context.getString(R.string.action_back)
        composeRule.onNodeWithContentDescription(backLabel).performClick()

        assert(wentBack) { "expected onBack() to be invoked" }
    }
}
