package com.landoulsi.catalog.shared.domain.usecase

import com.landoulsi.catalog.shared.core.DataResult
import com.landoulsi.catalog.shared.domain.model.Product
import com.landoulsi.catalog.shared.domain.repository.ProductRepository

class GetProductDetailsUseCase(
    private val productRepository: ProductRepository,
) {
    suspend operator fun invoke(id: Int): DataResult<Product> = productRepository.getProduct(id)
}
