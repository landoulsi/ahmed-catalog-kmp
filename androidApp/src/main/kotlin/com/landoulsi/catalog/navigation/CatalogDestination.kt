package com.landoulsi.catalog.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Type-safe navigation destinations implementing Navigation 3's [androidx.navigation3.runtime.NavKey].
 *
 * Serializable destinations ensure navigation state survives process death and
 * arguments are verified at compile time.
 */
sealed interface CatalogDestination : NavKey {

    @Serializable
    data object ProductList : CatalogDestination

    @Serializable
    data class ProductDetails(val productId: Int) : CatalogDestination

    @Serializable
    data object Favorites : CatalogDestination
}