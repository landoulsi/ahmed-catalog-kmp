package com.landoulsi.catalog.shared.fake

import com.landoulsi.catalog.shared.domain.model.Product
import com.landoulsi.catalog.shared.domain.repository.FavoriteProductsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/** In-memory [FavoriteProductsRepository] with the same emit-on-change contract. */
class FakeFavoriteProductsRepository(
    initial: List<Product> = emptyList(),
) : FavoriteProductsRepository {

    private val _favorites = MutableStateFlow(initial)

    override val favorites: Flow<List<Product>> = _favorites

    override fun isFavorite(productId: Int): Flow<Boolean> =
        _favorites.map { products -> products.any { it.id == productId } }

    override suspend fun toggleFavorite(product: Product) {
        val current = _favorites.value
        _favorites.value = if (current.any { it.id == product.id }) {
            current.filterNot { it.id == product.id }
        } else {
            listOf(product) + current
        }
    }
}
