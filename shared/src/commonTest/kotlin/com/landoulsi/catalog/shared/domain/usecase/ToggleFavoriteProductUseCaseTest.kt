package com.landoulsi.catalog.shared.domain.usecase

import com.landoulsi.catalog.shared.fake.RecordingFavoriteProductsRepository
import com.landoulsi.catalog.shared.fake.testProduct
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ToggleFavoriteProductUseCaseTest {

    private val repository = RecordingFavoriteProductsRepository()
    private val toggleFavorite = ToggleFavoriteProductUseCase(repository)

    @Test
    fun `forwards the complete product`() = runTest {
        val product = testProduct(id = 5, title = "Headphones", price = 79.99)

        toggleFavorite(product)

        assertEquals(listOf(product), repository.toggledProducts)
    }
}
