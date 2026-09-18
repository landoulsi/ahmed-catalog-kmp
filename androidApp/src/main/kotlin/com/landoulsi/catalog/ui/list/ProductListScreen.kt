package com.landoulsi.catalog.ui.list

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.landoulsi.catalog.R
import com.landoulsi.catalog.shared.presentation.products.ProductListUiState
import com.landoulsi.catalog.shared.presentation.products.ProductsViewModel
import com.landoulsi.catalog.ui.common.EmptyState
import com.landoulsi.catalog.ui.common.ErrorState
import com.landoulsi.catalog.ui.common.LoadingState
import com.landoulsi.catalog.ui.common.ProductRow
import com.landoulsi.catalog.ui.common.resolve
import com.landoulsi.catalog.ui.common.toUiText
import kotlinx.coroutines.flow.filter
import org.koin.compose.viewmodel.koinViewModel

/** Number of rows from the end at which the next page is requested. */
private const val LOAD_MORE_THRESHOLD = 4

/**
 * Only place that talks to Koin/the ViewModel for this screen — everything
 * below is plain state + callbacks, so [ProductListScreen] stays previewable
 * and testable without a DI graph.
 */
@Composable
fun ProductListRoute(
    onProductClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Scoped via CatalogNavHost's screenViewModelStoreOwner, so this
    // survives recomposition instead of resetting every time.
    val viewModel: ProductsViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProductListScreen(
        uiState = uiState,
        onQueryChange = { viewModel.onQueryChange(it) },
        onClearQuery = { viewModel.onClearQuery() },
        onProductClick = onProductClick,
        onToggleFavorite = { viewModel.onToggleFavorite(it) },
        onLoadMore = { viewModel.onLoadMore() },
        onRetry = { viewModel.onRetry() },
        modifier = modifier,
    )
}

/**
 * Stateless products list.
 *
 * Takes state and callbacks rather than a ViewModel, so it can be previewed and
 * UI-tested without any dependency graph.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProductListScreen(
    uiState: ProductListUiState,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    onProductClick: (Int) -> Unit,
    onToggleFavorite: (Int) -> Unit,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // rememberSaveable (not rememberLazyListState alone) so scroll position
    // survives leaving the composition on tab switch, not just recomposition.
    val listState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val isKeyboardVisible = WindowInsets.isImeVisible

    LocalOnBackPressedDispatcherOwner.current?.let {
        BackHandler(enabled = isKeyboardVisible || uiState.query.isNotEmpty()) {
            if (isKeyboardVisible) {
                keyboardController?.hide()
                focusManager.clearFocus()
            } else {
                focusManager.clearFocus()
                onClearQuery()
            }
        }
    }

    // Request the next page while the user is still a few rows from the end, so
    // pagination feels continuous rather than stop-start.
    //
    // The empty check matters: with no products, `lastVisible` is 0 and
    // `0 >= 0 - THRESHOLD` would be true, firing a page request before the
    // first load has even returned.
    val productCount = uiState.products.size
    val shouldLoadMore by remember(listState, productCount) {
        derivedStateOf {
            if (productCount == 0) {
                false
            } else {
                val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                lastVisible >= productCount - LOAD_MORE_THRESHOLD
            }
        }
    }

    // Restart after an append so the calculation uses the new item count. This
    // also keeps loading when a page is too short to fill the viewport.
    LaunchedEffect(listState, productCount) {
        snapshotFlow { shouldLoadMore }
            .filter { it }
            .collect { onLoadMore() }
    }

    Scaffold(
        modifier = modifier,
        topBar = { ProductsTopBar() },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(modifier = Modifier.fillMaxSize()) {
                ProductsSearchField(
                    query = uiState.query,
                    onQueryChange = onQueryChange,
                    onClearQuery = onClearQuery,
                )

                val error = uiState.error
                when {
                    uiState.isLoading -> LoadingState()  // padding comes from the enclosing Box

                    error != null && uiState.products.isEmpty() -> ErrorState(
                        message = error.toUiText().resolve(),
                        onRetry = onRetry,
                    )

                    uiState.isEmpty -> EmptyState(
                        title = if (uiState.query.isBlank()) {
                            stringResource(R.string.products_empty)
                        } else {
                            stringResource(R.string.products_empty_for_query, uiState.query)
                        },
                    )

                    else -> LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = dimensionResource(R.dimen.spacing_4x),
                            end = dimensionResource(R.dimen.spacing_4x),
                            top = dimensionResource(R.dimen.spacing_2x),
                            bottom = dimensionResource(R.dimen.spacing_6x),
                        ),
                        verticalArrangement = Arrangement.spacedBy(
                            dimensionResource(R.dimen.spacing_3x)
                        ),
                    ) {
                        items(items = uiState.products, key = { it.id }) { product ->
                            ProductRow(
                                product = product,
                                onClick = { onProductClick(product.id) },
                                onToggleFavorite = { onToggleFavorite(product.id) },
                            )
                        }

                        if (uiState.isLoadingMore) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(dimensionResource(R.dimen.spacing_4x)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(
                                            dimensionResource(R.dimen.progress_size)
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/** Top bar with title. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProductsTopBar(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(TopAppBarDefaults.windowInsets)
            .background(MaterialTheme.colorScheme.background)
            .padding(
                start = dimensionResource(R.dimen.spacing_4x),
                end = dimensionResource(R.dimen.spacing_4x),
                top = dimensionResource(R.dimen.spacing_3x),
                bottom = dimensionResource(R.dimen.spacing_1x),
            ),
    ) {
        Text(
            text = stringResource(R.string.products_title),
            style = MaterialTheme.typography.headlineSmall,
        )
    }
}

/** Search box with a clear action, shown above the product list. */
@Composable
internal fun ProductsSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(R.dimen.spacing_4x),
                vertical = dimensionResource(R.dimen.spacing_2x),
            ),
        placeholder = { Text(text = stringResource(R.string.products_search_hint)) },
        leadingIcon = {
            Icon(imageVector = Icons.Filled.Search, contentDescription = null)
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClearQuery) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = stringResource(R.string.products_search_clear),
                    )
                }
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        shape = RoundedCornerShape(dimensionResource(R.dimen.search_corner_radius)),
    )
}
