package com.landoulsi.catalog.shared.data

import app.cash.turbine.test
import com.landoulsi.catalog.shared.data.repository.FavoriteProductsRepositoryImpl
import com.landoulsi.catalog.shared.fake.FakeFavoriteProductsStorage
import com.landoulsi.catalog.shared.fake.TestDispatcherProvider
import com.landoulsi.catalog.shared.fake.testProduct
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FavoriteProductsRepositoryImplTest {

    @Test
    fun `restores previously persisted favorites on creation`() = runTest {
        val storage = FakeFavoriteProductsStorage(initial = listOf(testProduct(id = 3, title = "Saved")))

        val repository = FavoriteProductsRepositoryImpl(storage, TestDispatcherProvider())

        val favorites = repository.favorites.first()
        assertEquals(listOf(3), favorites.map { it.id })
        assertEquals("Saved", favorites.single().title)
    }

    @Test
    fun `toggling an unsaved product adds it and persists it`() = runTest {
        val storage = FakeFavoriteProductsStorage()
        val repository = FavoriteProductsRepositoryImpl(storage, TestDispatcherProvider())

        repository.toggleFavorite(testProduct(id = 1))

        assertEquals(listOf(1), repository.favorites.first().map { it.id })
        assertEquals(listOf(1), storage.stored.map { it.id })
    }

    @Test
    fun `toggling a saved product removes it and persists the removal`() = runTest {
        val storage = FakeFavoriteProductsStorage()
        val repository = FavoriteProductsRepositoryImpl(storage, TestDispatcherProvider())
        val product = testProduct(id = 1)

        repository.toggleFavorite(product)
        repository.toggleFavorite(product)

        assertTrue(repository.favorites.first().isEmpty())
        assertTrue(storage.stored.isEmpty())
    }

    @Test
    fun `orders favorites newest first`() = runTest {
        val repository = FavoriteProductsRepositoryImpl(FakeFavoriteProductsStorage(), TestDispatcherProvider())

        repository.toggleFavorite(testProduct(id = 1))
        repository.toggleFavorite(testProduct(id = 2))

        assertEquals(listOf(2, 1), repository.favorites.first().map { it.id })
    }

    @Test
    fun `tracks whether a specific product is inFavorite`() = runTest {
        val repository = FavoriteProductsRepositoryImpl(FakeFavoriteProductsStorage(), TestDispatcherProvider())

        // Turbine asserts the emission sequence, not just the final value: the
        // observer must see false first, then true the moment the toggle lands.
        repository.isFavorite(productId = 9).test {
            assertEquals(false, awaitItem())

            repository.toggleFavorite(testProduct(id = 9))
            assertEquals(true, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits every intermediate state as favorites are added and removed`() = runTest {
        val repository = FavoriteProductsRepositoryImpl(FakeFavoriteProductsStorage(), TestDispatcherProvider())
        val product = testProduct(id = 1)

        repository.favorites.test {
            assertEquals(emptyList(), awaitItem())

            repository.toggleFavorite(product)
            assertEquals(listOf(1), awaitItem().map { it.id })

            repository.toggleFavorite(product)
            assertEquals(emptyList(), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }
}
