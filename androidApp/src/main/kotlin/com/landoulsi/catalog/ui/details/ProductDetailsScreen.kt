package com.landoulsi.catalog.ui.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.landoulsi.catalog.R
import com.landoulsi.catalog.shared.presentation.details.ProductDetailsUiState
import com.landoulsi.catalog.shared.presentation.details.ProductDetailsViewModel
import com.landoulsi.catalog.ui.common.ErrorState
import com.landoulsi.catalog.ui.common.LoadingState
import com.landoulsi.catalog.ui.common.resolve
import com.landoulsi.catalog.ui.common.toUiText
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ProductDetailsRoute(
    productId: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Explicit `key` so Koin caches one ViewModel per product, not per
    // class — otherwise product 5's instance could leak into product 9's.
    val viewModel: ProductDetailsViewModel = koinViewModel(
        key = "product-$productId",
        parameters = { parametersOf(productId) },
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProductDetailsScreen(
        uiState = uiState,
        onBack = onBack,
        onToggleFavorite = viewModel::onToggleFavorite,
        onRetry = viewModel::onRetry,
        modifier = modifier,
    )
}

/** Stateless product details. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    uiState: ProductDetailsUiState,
    onBack: () -> Unit,
    onToggleFavorite: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            ProductDetailsTopBar(
                title = uiState.content?.title,
                isFavorite = uiState.isFavorite,
                showFavoriteAction = uiState.content != null,
                onBack = onBack,
                onToggleFavorite = onToggleFavorite,
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        val content = uiState.content
        val error = uiState.error
        when {
            uiState.isLoading -> LoadingState(modifier = Modifier.padding(padding))

            error != null -> ErrorState(
                message = error.toUiText().resolve(),
                onRetry = onRetry,
                modifier = Modifier.padding(padding),
            )

            content != null -> SelectionContainer {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = dimensionResource(R.dimen.spacing_4x))
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(
                        dimensionResource(R.dimen.spacing_4x)
                    ),
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = dimensionResource(R.dimen.spacing_2x)),
                        shape = RoundedCornerShape(dimensionResource(R.dimen.hero_corner_radius)),
                        color = MaterialTheme.colorScheme.surfaceContainer,
                    ) {
                        AsyncImage(
                            model = content.imageUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(dimensionResource(R.dimen.product_hero_height))
                                .padding(dimensionResource(R.dimen.spacing_4x)),
                        )
                    }

                    Column(
                        modifier = Modifier.padding(
                            bottom = dimensionResource(R.dimen.spacing_6x),
                        ),
                        verticalArrangement = Arrangement.spacedBy(
                            dimensionResource(R.dimen.spacing_3x)
                        ),
                    ) {
                        Text(
                            text = content.title,
                            style = MaterialTheme.typography.headlineMedium,
                        )

                        if (content.brand.isNotBlank()) {
                            Text(
                                text = stringResource(R.string.details_brand, content.brand),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(
                                dimensionResource(R.dimen.spacing_2x)
                            ),
                        ) {
                            Text(
                                text = content.formattedPrice,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            if (content.hasDiscount) {
                                Text(
                                    text = stringResource(
                                        R.string.discount_format,
                                        content.formattedDiscount,
                                    ),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.error,
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(
                                dimensionResource(R.dimen.spacing_2x)
                            ),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = stringResource(R.string.details_rating),
                                modifier = Modifier.size(dimensionResource(R.dimen.icon_size_4x)),
                                tint = MaterialTheme.colorScheme.tertiary,
                            )
                            Text(
                                text = stringResource(
                                    R.string.rating_format,
                                    content.formattedRating,
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            SuggestionChip(
                                // Non-interactive: a status indicator, not an action.
                                onClick = {},
                                enabled = false,
                                label = {
                                    Text(
                                        text = if (content.isInStock) {
                                            stringResource(
                                                R.string.details_stock_count,
                                                content.stock,
                                            )
                                        } else {
                                            stringResource(R.string.details_out_of_stock)
                                        }
                                    )
                                },
                            )
                        }

                        if (content.category.isNotBlank()) {
                            Text(
                                text = content.category,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }

                        Text(
                            text = stringResource(R.string.details_description),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(
                                top = dimensionResource(R.dimen.spacing_3x),
                            ),
                        )
                        Surface(
                            shape = RoundedCornerShape(
                                dimensionResource(R.dimen.card_corner_radius),
                            ),
                            color = MaterialTheme.colorScheme.surfaceContainerLow,
                        ) {
                            Text(
                                text = content.description,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(dimensionResource(R.dimen.spacing_4x)),
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Top bar with the product title, a back action, and a favorite toggle. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProductDetailsTopBar(
    title: String?,
    isFavorite: Boolean,
    showFavoriteAction: Boolean,
    onBack: () -> Unit,
    onToggleFavorite: () -> Unit,
) {
    TopAppBar(
        title = { Text(text = title ?: stringResource(R.string.details_title)) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.action_back),
                )
            }
        },
        actions = {
            if (showFavoriteAction) {
                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector = if (isFavorite) {
                            Icons.Filled.Star
                        } else {
                            Icons.Outlined.StarBorder
                        },
                        contentDescription = stringResource(
                            if (isFavorite) R.string.favorite_remove else R.string.favorite_add
                        ),
                        tint = if (isFavorite) {
                            MaterialTheme.colorScheme.tertiary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
    )
}
