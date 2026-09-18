package com.landoulsi.catalog.shared.fake

import com.landoulsi.catalog.shared.core.DataResult
import com.landoulsi.catalog.shared.domain.model.Product
import com.landoulsi.catalog.shared.domain.model.ProductPage
import com.landoulsi.catalog.shared.domain.repository.FavoriteProductsRepository
import com.landoulsi.catalog.shared.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

data class ProductCall(val query: String, val skip: Int, val limit: Int)

/** Records the exact arguments and returns a fixed result, so a use-case test can assert on pass-through rather than behavior. */
class RecordingProductRepository(
    private val productsResult: DataResult<ProductPage> = DataResult.Success(
        ProductPage(products = emptyList(), total = 0, skip = 0, limit = 20),
    ),
    private val detailsResult: DataResult<Product> = DataResult.Success(testProduct()),
) : ProductRepository {

    val productCalls = mutableListOf<ProductCall>()
    val detailCalls = mutableListOf<Int>()

    override suspend fun getProducts(
        query: String,
        skip: Int,
        limit: Int,
    ): DataResult<ProductPage> {
        productCalls += ProductCall(query, skip, limit)
        return productsResult
    }

    override suspend fun getProduct(id: Int): DataResult<Product> {
        detailCalls += id
        return detailsResult
    }
}

class RecordingFavoriteProductsRepository : FavoriteProductsRepository {
    val favoritesFlow = MutableStateFlow<List<Product>>(emptyList())
    val isFavoriteFlow = MutableStateFlow(false)
    var getFavoriteProductsCalls = 0
    val observedProductIds = mutableListOf<Int>()
    val toggledProducts = mutableListOf<Product>()

    override val favorites: Flow<List<Product>>
        get() {
            getFavoriteProductsCalls += 1
            return favoritesFlow
        }

    override fun isFavorite(productId: Int): Flow<Boolean> {
        observedProductIds += productId
        return isFavoriteFlow
    }

    override suspend fun toggleFavorite(product: Product) {
        toggledProducts += product
    }
}
