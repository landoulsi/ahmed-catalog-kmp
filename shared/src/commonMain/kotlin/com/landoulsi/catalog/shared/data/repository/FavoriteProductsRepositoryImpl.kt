package com.landoulsi.catalog.shared.data.repository

import com.landoulsi.catalog.shared.core.DispatcherProvider
import com.landoulsi.catalog.shared.data.local.FavoriteProductsStorage
import com.landoulsi.catalog.shared.domain.model.Product
import com.landoulsi.catalog.shared.domain.repository.FavoriteProductsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * [FavoriteProductsRepository] backed by [FavoriteProductsStorage].
 *
 * Storage is read once at construction into a [MutableStateFlow], which then
 * acts as the single source of truth: every screen observes the same flow, so a
 * toggle on the details screen is reflected in the list and favorites screens
 * immediately. Writes go to disk and to the flow together.
 *
 * The initial read is synchronous and deliberately so: favorites are a small,
 * user-curated list (a handful to a few dozen rows), and reading it eagerly
 * means the flow's first emission is always the real data — no consumer needs
 * a "still loading favorites" placeholder for what is, in practice, a
 * sub-millisecond read.
 */
class FavoriteProductsRepositoryImpl(
    private val favoriteProductsStorage: FavoriteProductsStorage,
    private val dispatcherProvider: DispatcherProvider,
) : FavoriteProductsRepository {

    private val _favorites = MutableStateFlow(favoriteProductsStorage.read())

    /** Serializes read-modify-write cycles so concurrent toggles cannot race. */
    private val writeMutex = Mutex()

    override val favorites: Flow<List<Product>> = _favorites.asStateFlow()

    override fun isFavorite(productId: Int): Flow<Boolean> =
        _favorites.map { products -> products.any { it.id == productId } }
            .distinctUntilChanged()

    override suspend fun toggleFavorite(product: Product) = writeMutex.withLock {
        val current = _favorites.value
        val updated = if (current.any { it.id == product.id }) {
            current.filterNot { it.id == product.id }
        } else {
            // Newest first, so the favorites screen shows recent saves on top.
            listOf(product) + current
        }

        withContext(dispatcherProvider.io) {
            favoriteProductsStorage.write(updated)
        }
        _favorites.value = updated
    }
}
