package com.landoulsi.catalog.shared.domain.usecase

import com.landoulsi.catalog.shared.domain.model.Product
import com.landoulsi.catalog.shared.domain.repository.FavoriteProductsRepository
import kotlinx.coroutines.flow.Flow

class GetFavoriteProductsUseCase(
    private val favoriteProductsRepository: FavoriteProductsRepository,
) {
    operator fun invoke(): Flow<List<Product>> = favoriteProductsRepository.favorites
}
