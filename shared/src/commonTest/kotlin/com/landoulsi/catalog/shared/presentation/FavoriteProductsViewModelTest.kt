package com.landoulsi.catalog.shared.presentation

import app.cash.turbine.test
import com.landoulsi.catalog.shared.domain.usecase.GetFavoriteProductsUseCase
import com.landoulsi.catalog.shared.domain.usecase.ToggleFavoriteProductUseCase
import com.landoulsi.catalog.shared.fake.FakeFavoriteProductsRepository
import com.landoulsi.catalog.shared.fake.FakeProductFormatter
import com.landoulsi.catalog.shared.fake.TestDispatcherProvider
import com.landoulsi.catalog.shared.fake.testProduct
import com.landoulsi.catalog.shared.presentation.favorites.FavoriteProductsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteProductsViewModelTest {

    private val productFormatter = FakeProductFormatter()

    private fun TestScope.viewModel(repository: FakeFavoriteProductsRepository) = FavoriteProductsViewModel(
        toggleFavorite = ToggleFavoriteProductUseCase(repository),
        getFavoriteProducts = GetFavoriteProductsUseCase(repository),
        dispatcherProvider = TestDispatcherProvider(UnconfinedTestDispatcher(testScheduler)),
        productFormatter = productFormatter,
    )

    @Test
    fun `emits persisted favorites`() = runTest {
        val repository = FakeFavoriteProductsRepository(
            initial = listOf(testProduct(id = 1), testProduct(id = 2))
        )

        val viewModel = viewModel(repository)
        advanceUntilIdle()

        assertEquals(listOf(1, 2), viewModel.uiState.value.favorites.map { it.id })
        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    @Test
    fun `every favorite is marked isFavorite even though the repository model has no such flag`() = runTest {
        val repository = FakeFavoriteProductsRepository(initial = listOf(testProduct(id = 1)))

        val viewModel = viewModel(repository)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.favorites.single().isFavorite)
    }

    @Test
    fun `reports an empty state when nothing is saved`() = runTest {
        val viewModel = viewModel(FakeFavoriteProductsRepository())
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isEmpty)
    }

    @Test
    fun `removing a favorite updates the list without an explicit refresh`() = runTest {
        val product = testProduct(id = 1)
        val repository = FakeFavoriteProductsRepository(initial = listOf(product))
        val viewModel = viewModel(repository)
        advanceUntilIdle()

        viewModel.onRemoveFavorite(product.id)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.favorites.isEmpty())
    }

    @Test
    fun `state transitions from loading to loaded in order`() = runTest {
        val repository = FakeFavoriteProductsRepository(initial = listOf(testProduct(id = 1)))
        // StandardTestDispatcher queues rather than running eagerly, so the
        // initial isLoading = true state is actually observable here — with
        // the unconfined dispatcher the other tests use, it is skipped before
        // a collector could ever see it.
        val viewModel = FavoriteProductsViewModel(
            toggleFavorite = ToggleFavoriteProductUseCase(repository),
            getFavoriteProducts = GetFavoriteProductsUseCase(repository),
            dispatcherProvider = TestDispatcherProvider(StandardTestDispatcher(testScheduler)),
            productFormatter = productFormatter,
        )

        viewModel.uiState.test {
            assertTrue(awaitItem().isLoading)

            val loaded = awaitItem()
            assertEquals(false, loaded.isLoading)
            assertEquals(listOf(1), loaded.favorites.map { it.id })

            cancelAndIgnoreRemainingEvents()
        }
    }
}
