package com.landoulsi.catalog.ui.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.landoulsi.catalog.R
import com.landoulsi.catalog.shared.presentation.favorites.FavoriteProductsUiState
import com.landoulsi.catalog.shared.presentation.favorites.FavoriteProductsViewModel
import com.landoulsi.catalog.ui.common.EmptyState
import com.landoulsi.catalog.ui.common.LoadingState
import com.landoulsi.catalog.ui.common.ProductRow
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FavoriteProductsRoute(
    onProductClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: FavoriteProductsViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    FavoriteProductsScreen(
        uiState = uiState,
        onProductClick = onProductClick,
        onRemoveFavorite = viewModel::onRemoveFavorite,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteProductsScreen(
    uiState: FavoriteProductsUiState,
    onProductClick: (Int) -> Unit,
    onRemoveFavorite: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = { FavoriteProductsTopBar() },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        when {
            uiState.isLoading -> LoadingState(modifier = Modifier.padding(padding))

            uiState.isEmpty -> EmptyState(
                title = stringResource(R.string.favorites_empty),
                subtitle = stringResource(R.string.favorites_empty_hint),
                modifier = Modifier.padding(padding),
            )

            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(
                    horizontal = dimensionResource(R.dimen.spacing_4x),
                    vertical = dimensionResource(R.dimen.spacing_2x),
                ),
                verticalArrangement = Arrangement.spacedBy(
                    dimensionResource(R.dimen.spacing_3x)
                ),
            ) {
                items(items = uiState.favorites, key = { it.id }) { product ->
                    ProductRow(
                        product = product,
                        onClick = { onProductClick(product.id) },
                        onToggleFavorite = { onRemoveFavorite(product.id) },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FavoriteProductsTopBar() {
    TopAppBar(
        title = { Text(text = stringResource(R.string.favorites_title)) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
    )
}
