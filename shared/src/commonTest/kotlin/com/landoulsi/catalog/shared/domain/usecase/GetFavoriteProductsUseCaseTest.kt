package com.landoulsi.catalog.shared.domain.usecase

import com.landoulsi.catalog.shared.fake.RecordingFavoriteProductsRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class GetFavoriteProductsUseCaseTest {

    private val repository = RecordingFavoriteProductsRepository()
    private val getFavoriteProducts = GetFavoriteProductsUseCase(repository)

    @Test
    fun `exposes the repository flow`() {
        val actual = getFavoriteProducts()

        assertSame(repository.favoritesFlow, actual)
        assertEquals(1, repository.getFavoriteProductsCalls)
    }
}
