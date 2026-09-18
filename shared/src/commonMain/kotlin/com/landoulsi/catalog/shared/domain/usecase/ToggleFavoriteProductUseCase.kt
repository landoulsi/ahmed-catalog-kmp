package com.landoulsi.catalog.shared.domain.usecase

import com.landoulsi.catalog.shared.domain.model.Product
import com.landoulsi.catalog.shared.domain.repository.FavoriteProductsRepository

class ToggleFavoriteProductUseCase(
    private val favoriteProductsRepository: FavoriteProductsRepository,
) {
    suspend operator fun invoke(product: Product) = favoriteProductsRepository.toggleFavorite(product)
}
