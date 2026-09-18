package com.landoulsi.catalog.shared.data.repository

import com.landoulsi.catalog.shared.core.DataResult
import com.landoulsi.catalog.shared.core.Logger
import com.landoulsi.catalog.shared.data.mapper.ProductMapper
import com.landoulsi.catalog.shared.data.remote.CatalogApi
import com.landoulsi.catalog.shared.data.remote.toCatalogError
import com.landoulsi.catalog.shared.domain.model.Product
import com.landoulsi.catalog.shared.domain.model.ProductPage
import com.landoulsi.catalog.shared.domain.repository.ProductRepository
import kotlinx.coroutines.CancellationException

/**
 * Chooses between the list and search endpoints based on the query.
 */
class ProductRepositoryImpl(
    private val catalogApi: CatalogApi,
    private val productMapper: ProductMapper,
    private val logger: Logger,
) : ProductRepository {

    override suspend fun getProducts(
        query: String,
        skip: Int,
        limit: Int,
    ): DataResult<ProductPage> = runCatchingCatalogError {
        if (query.isBlank()) {
            productMapper.toDomain(catalogApi.getProducts(skip = skip, limit = limit))
        } else {
            productMapper.toDomain(
                catalogApi.searchProducts(query = query, skip = skip, limit = limit),
            )
        }
    }

    override suspend fun getProduct(id: Int): DataResult<Product> = runCatchingCatalogError {
        productMapper.toDomain(catalogApi.getProduct(id))
    }

    /**
     * Runs [block], converting any transport failure into [DataResult.Failure].
     *
     * [CancellationException] is rethrown rather than wrapped, so structured
     * concurrency (cancelling a stale request) keeps working.
     */
    private inline fun <T> runCatchingCatalogError(block: () -> T): DataResult<T> = try {
        DataResult.Success(block())
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (throwable: Throwable) {
        logger.error(throwable, "Product request failed")
        DataResult.Failure(throwable.toCatalogError())
    }
}
