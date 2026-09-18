package com.landoulsi.catalog.shared.domain.usecase

import com.landoulsi.catalog.shared.domain.repository.FavoriteProductsRepository
import kotlinx.coroutines.flow.Flow

class IsFavoriteProductUseCase(
    private val favoriteProductsRepository: FavoriteProductsRepository,
) {
    operator fun invoke(productId: Int): Flow<Boolean> =
        favoriteProductsRepository.isFavorite(productId)
}
