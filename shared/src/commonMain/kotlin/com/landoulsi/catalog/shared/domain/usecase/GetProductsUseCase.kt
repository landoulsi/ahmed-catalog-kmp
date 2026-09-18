package com.landoulsi.catalog.shared.domain.usecase

import com.landoulsi.catalog.shared.core.DataResult
import com.landoulsi.catalog.shared.domain.model.ProductPage
import com.landoulsi.catalog.shared.domain.repository.ProductRepository

/**
 * Search and plain listing differ only by the presence of a query, so they share
 * one use case rather than duplicating paging rules.
 */
class GetProductsUseCase(
    private val productRepository: ProductRepository,
) {
    suspend operator fun invoke(
        query: String = "",
        skip: Int = 0,
        limit: Int = DEFAULT_PAGE_SIZE,
    ): DataResult<ProductPage> =
        productRepository.getProducts(query = query.trim(), skip = skip, limit = limit)

    companion object {
        const val DEFAULT_PAGE_SIZE = 20
    }
}
