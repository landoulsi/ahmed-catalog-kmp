package com.landoulsi.catalog.shared.domain.repository

import com.landoulsi.catalog.shared.core.DataResult
import com.landoulsi.catalog.shared.domain.model.Product
import com.landoulsi.catalog.shared.domain.model.ProductPage

interface ProductRepository {

    /**
     * One page of products. When [query] is non-blank the page is restricted to
     * products matching it; otherwise the full catalog is paged.
     */
    suspend fun getProducts(query: String, skip: Int, limit: Int): DataResult<ProductPage>

    suspend fun getProduct(id: Int): DataResult<Product>
}
