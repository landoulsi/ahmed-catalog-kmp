package com.landoulsi.catalog.ui.list

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextInputSelection
import androidx.compose.ui.text.TextRange
import androidx.test.platform.app.InstrumentationRegistry
import com.landoulsi.catalog.R
import com.landoulsi.catalog.shared.presentation.common.ErrorMessage
import com.landoulsi.catalog.shared.presentation.products.ProductListUiState
import com.landoulsi.catalog.ui.testProductUiState
import com.landoulsi.catalog.ui.theme.ProductCatalogTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Screen-level tests: asserts what composables are emitted, what text
 * the screen renders for a given [ProductListUiState] and which callback fires for
 * each user action.
 */
class ProductListScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context: Context
        get() = InstrumentationRegistry.getInstrumentation().targetContext

    private fun setContent(
        uiState: ProductListUiState,
        onQueryChange: (String) -> Unit = {},
        onClearQuery: () -> Unit = {},
        onProductClick: (Int) -> Unit = {},
        onToggleFavorite: (Int) -> Unit = {},
        onLoadMore: () -> Unit = {},
        onRetry: () -> Unit = {},
    ) {
        composeRule.setContent {
            ProductCatalogTheme {
                ProductListScreen(
                    uiState = uiState,
                    onQueryChange = onQueryChange,
                    onClearQuery = onClearQuery,
                    onProductClick = onProductClick,
                    onToggleFavorite = onToggleFavorite,
                    onLoadMore = onLoadMore,
                    onRetry = onRetry,
                )
            }
        }
    }

    @Test
    fun showsEachProductTitleInTheList() {
        setContent(
            uiState = ProductListUiState(
                products = listOf(
                    testProductUiState(id = 1, title = PHONE_TITLE),
                    testProductUiState(id = 2, title = LAPTOP_TITLE),
                ),
            ),
        )

        composeRule.onNodeWithText(PHONE_TITLE).assertExists()
        composeRule.onNodeWithText(LAPTOP_TITLE).assertExists()
    }

    @Test
    fun showsEmptyStateWhenNoProductsAndNoQuery() {
        setContent(uiState = ProductListUiState(products = emptyList()))

        composeRule.onNodeWithText(
            context.getString(R.string.products_empty)
        ).assertExists()
    }

    @Test
    fun showsNoResultsStateWhenQueryProducesNothing() {
        setContent(uiState = ProductListUiState(query = QUERY_WITH_NO_RESULTS, products = emptyList()))

        composeRule.onNodeWithText(
            context.getString(R.string.products_empty_for_query, QUERY_WITH_NO_RESULTS)
        ).assertExists()
    }

    @Test
    fun showsSearchBar() {
        setContent(uiState = ProductListUiState())

        composeRule.onNodeWithText(
            context.getString(R.string.products_search_hint)
        ).assertExists()
    }

    @Test
    fun typingInSearchBarReportsQuery() {
        var query = ""
        setContent(
            uiState = ProductListUiState(query = "phone"),
            onQueryChange = { query = it },
        )

        val field = composeRule.onNodeWithText("phone")
        field.performTextInputSelection(TextRange("phone".length))
        field.performTextInput("s")

        assert(query == "phones") { "expected onQueryChange(\"phones\"), got \"$query\"" }
    }

    @Test
    fun tappingClearButtonClearsTheQuery() {
        var cleared = false
        setContent(
            uiState = ProductListUiState(query = "phone"),
            onClearQuery = { cleared = true },
        )

        val clearDescription = context.getString(R.string.products_search_clear)
        composeRule.onNodeWithContentDescription(clearDescription).performClick()

        assert(cleared) { "expected onClearQuery to be called" }
    }

    @Test
    fun tappingAProductRowReportsItsId() {
        var clickedId: Int? = null
        setContent(
            uiState = ProductListUiState(products = listOf(testProductUiState(id = 42, title = TABLET_TITLE))),
            onProductClick = { clickedId = it },
        )

        composeRule.onNodeWithText(TABLET_TITLE).performClick()

        assert(clickedId == 42) { "expected onProductClick(42), got $clickedId" }
    }

    @Test
    fun tappingTheFavoriteIconReportsTheProduct() {
        var toggled: Int? = null
        val product = testProductUiState(id = 7, title = CAMERA_TITLE)
        setContent(
            uiState = ProductListUiState(products = listOf(product)),
            onToggleFavorite = { toggled = it },
        )

        val addDescription = context.getString(R.string.favorite_add)
        composeRule.onNodeWithContentDescription(addDescription).performClick()

        assert(toggled == 7) { "expected onToggleFavorite(product #7), got $toggled" }
    }

    @Test
    fun inFavoriteProductShowsRemoveDescriptionInsteadOfAdd() {
        val product = testProductUiState(id = 3, title = WATCH_TITLE, isFavorite = true)
        setContent(
            uiState = ProductListUiState(products = listOf(product)),
        )

        val removeDescription = context.getString(R.string.favorite_remove)
        composeRule.onNodeWithContentDescription(removeDescription).assertExists()
    }

    @Test
    fun showsErrorStateWhenLoadingFails() {
        setContent(
            uiState = ProductListUiState(products = emptyList(), error = ErrorMessage.Network),
        )

        composeRule.onNodeWithText(
            context.getString(R.string.error_network)
        ).assertExists()
    }

    @Test
    fun tappingRetryInErrorStateReportsRetry() {
        var retried = false

        composeRule.setContent {
            ProductCatalogTheme {
                ProductListScreen(
                    uiState = ProductListUiState(error = ErrorMessage.Server),
                    onQueryChange = {},
                    onClearQuery = {},
                    onProductClick = {},
                    onToggleFavorite = {},
                    onLoadMore = {},
                    onRetry = { retried = true },
                )
            }
        }

        val retryText = context.getString(R.string.action_retry)
        composeRule.onNodeWithText(retryText).performClick()

        assert(retried) { "expected onRetry to be called" }
    }

    @Test
    fun scrollingNearTheBottomTriggersLoadMore() {
        var loadMoreCalls = 0
        var uiState by mutableStateOf(ProductListUiState(isLoading = true))

        composeRule.setContent {
            ProductCatalogTheme {
                ProductListScreen(
                    uiState = uiState,
                    onQueryChange = {},
                    onClearQuery = {},
                    onProductClick = {},
                    onToggleFavorite = {},
                    onLoadMore = { loadMoreCalls++ },
                    onRetry = {},
                )
            }
        }

        composeRule.runOnIdle {
            uiState = ProductListUiState(
                products = (1..30).map { id -> testProductUiState(id = id) },
                canLoadMore = true,
            )
        }
        composeRule.onNode(hasScrollAction()).performScrollToIndex(29)
        composeRule.waitForIdle()

        assertTrue("expected scrolling near the end to request another page", loadMoreCalls > 0)
    }

    private companion object {
        const val PHONE_TITLE = "Phone"
        const val LAPTOP_TITLE = "Laptop"
        const val TABLET_TITLE = "Tablet"
        const val CAMERA_TITLE = "Camera"
        const val WATCH_TITLE = "Watch"
        const val SEARCH_QUERY = "phone"
        const val QUERY_WITH_NO_RESULTS = "zzz"
    }
}
