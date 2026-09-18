package com.landoulsi.catalog.shared.domain.usecase

import com.landoulsi.catalog.shared.fake.RecordingFavoriteProductsRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class IsFavoriteProductUseCaseTest {

    private val repository = RecordingFavoriteProductsRepository()
    private val isFavorite = IsFavoriteProductUseCase(repository)

    @Test
    fun `forwards product id and exposes repository flow`() {
        val actual = isFavorite(productId = 13)

        assertSame(repository.isFavoriteFlow, actual)
        assertEquals(listOf(13), repository.observedProductIds)
    }
}
