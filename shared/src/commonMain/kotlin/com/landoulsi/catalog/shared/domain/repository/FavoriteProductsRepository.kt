package com.landoulsi.catalog.shared.domain.repository

import com.landoulsi.catalog.shared.domain.model.Product
import kotlinx.coroutines.flow.Flow

/**
 * Exposes [Flow] so every screen observing favorites updates the moment one is
 * toggled anywhere in the app.
 */
interface FavoriteProductsRepository {

    /** All favorites, newest first, emitting again on every change. */
    val favorites: Flow<List<Product>>

    /** Whether [productId] is currently a favorite, emitting again on change. */
    fun isFavorite(productId: Int): Flow<Boolean>

    suspend fun toggleFavorite(product: Product)
}
