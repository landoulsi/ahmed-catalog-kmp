package com.landoulsi.catalog.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.landoulsi.catalog.R
import com.landoulsi.catalog.ui.details.ProductDetailsRoute
import com.landoulsi.catalog.ui.favorites.FavoriteProductsRoute
import com.landoulsi.catalog.ui.list.ProductListRoute

/** Root navigation hub and screen layout controller for the app. */
@Composable
fun CatalogNavHost(
    modifier: Modifier = Modifier,
) {
    // One back stack per tab, so switching tabs preserves each tab's position.
    val productsBackStack = rememberNavBackStack(CatalogDestination.ProductList)
    val favoritesBackStack = rememberNavBackStack(CatalogDestination.Favorites)
    var activeTab by remember { mutableStateOf(CatalogTab.ProductList) }
    val backStack = if (activeTab == CatalogTab.Favorites) favoritesBackStack else productsBackStack

    // Keeps Products/Favorites ViewModels alive across tab switches.
    // ProductDetails skips this — scoped per product instead.
    val screenViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current)

    Scaffold(
        modifier = modifier,
        bottomBar = {
            CatalogBottomBar(
                activeTab = activeTab,
                onTabSelected = { tab -> activeTab = tab },
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        NavDisplay(
            backStack = backStack,
            modifier = Modifier.padding(bottom = padding.calculateBottomPadding()),
            entryDecorators = listOf(
                // Preserves rememberSaveable state (e.g. scroll position) per entry.
                rememberSaveableStateHolderNavEntryDecorator(),
                // Avoids the ViewModel dying when the tab is left.
                rememberViewModelStoreNavEntryDecorator(),
            ),
            entryProvider = { key ->
                when (key) {
                    is CatalogDestination.ProductList -> NavEntry(key) {
                        CompositionLocalProvider(LocalViewModelStoreOwner provides screenViewModelStoreOwner) {
                            ProductListRoute(
                                onProductClick = { productId ->
                                    backStack.add(CatalogDestination.ProductDetails(productId))
                                },
                            )
                        }
                    }

                    is CatalogDestination.ProductDetails -> NavEntry(key) {
                        ProductDetailsRoute(
                            productId = key.productId,
                            onBack = { backStack.removeLastOrNull() },
                        )
                    }

                    is CatalogDestination.Favorites -> NavEntry(key) {
                        CompositionLocalProvider(LocalViewModelStoreOwner provides screenViewModelStoreOwner) {
                            FavoriteProductsRoute(
                                onProductClick = { productId ->
                                    backStack.add(CatalogDestination.ProductDetails(productId))
                                },
                            )
                        }
                    }

                    else -> error("Unknown destination: $key")
                }
            },
        )
    }
}

/** Bottom navigation bar switching between the Products list and Favorites tabs. */
@Composable
private fun CatalogBottomBar(
    activeTab: CatalogTab,
    onTabSelected: (CatalogTab) -> Unit,
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        NavigationBarItem(
            selected = activeTab == CatalogTab.ProductList,
            onClick = { onTabSelected(CatalogTab.ProductList) },
            icon = { Icon(imageVector = Icons.AutoMirrored.Filled.List, contentDescription = null) },
            label = { Text(text = stringResource(R.string.nav_products)) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
            ),
        )
        NavigationBarItem(
            selected = activeTab == CatalogTab.Favorites,
            onClick = { onTabSelected(CatalogTab.Favorites) },
            icon = { Icon(imageVector = Icons.Filled.Star, contentDescription = null) },
            label = { Text(text = stringResource(R.string.nav_favorites)) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onTertiaryContainer,
                selectedTextColor = MaterialTheme.colorScheme.tertiary,
                indicatorColor = MaterialTheme.colorScheme.tertiaryContainer,
            ),
        )
    }
}

