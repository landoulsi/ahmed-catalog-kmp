package com.landoulsi.catalog.shared.presentation

import com.landoulsi.catalog.shared.core.CatalogError
import com.landoulsi.catalog.shared.domain.usecase.GetFavoriteProductsUseCase
import com.landoulsi.catalog.shared.domain.usecase.GetProductsUseCase
import com.landoulsi.catalog.shared.domain.usecase.ToggleFavoriteProductUseCase
import com.landoulsi.catalog.shared.fake.FakeFavoriteProductsRepository
import com.landoulsi.catalog.shared.fake.FakeProductFormatter
import com.landoulsi.catalog.shared.fake.FakeProductRepository
import com.landoulsi.catalog.shared.fake.TestDispatcherProvider
import com.landoulsi.catalog.shared.presentation.common.ErrorMessage
import com.landoulsi.catalog.shared.presentation.products.ProductsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ProductsViewModelTest {

    private val favoriteProductsRepository = FakeFavoriteProductsRepository()
    private val productFormatter = FakeProductFormatter()

    private fun TestScope.viewModel(repository: FakeProductRepository) = ProductsViewModel(
        getProducts = GetProductsUseCase(repository),
        toggleFavorite = ToggleFavoriteProductUseCase(favoriteProductsRepository),
        getFavoriteProducts = GetFavoriteProductsUseCase(favoriteProductsRepository),
        dispatcherProvider = TestDispatcherProvider(UnconfinedTestDispatcher(testScheduler)),
        productFormatter = productFormatter,
    )

    @Test
    fun `loads the first page on creation`() = runTest {
        val repository = FakeProductRepository(total = 100)

        val viewModel = viewModel(repository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(20, state.products.size)
        assertEquals("10.00", state.products.first().formattedPrice)
        assertFalse(state.isLoading)
        assertTrue(state.canLoadMore)
        assertEquals(
            FakeProductRepository.Call(query = "", skip = 0, limit = 20),
            repository.calls.single(),
        )
    }

    @Test
    fun `appends the next page and advances the cursor`() = runTest {
        val repository = FakeProductRepository(total = 100)
        val viewModel = viewModel(repository)
        advanceUntilIdle()

        viewModel.onLoadMore()
        advanceUntilIdle()

        assertEquals(40, viewModel.uiState.value.products.size)
        assertEquals(listOf(0, 20), repository.calls.map { it.skip })
    }

    @Test
    fun `stops paging once the catalog is exhausted`() = runTest {
        // Exactly one page of results available.
        val repository = FakeProductRepository(total = 20)
        val viewModel = viewModel(repository)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.canLoadMore)

        viewModel.onLoadMore()
        advanceUntilIdle()

        // No second request was made.
        assertEquals(1, repository.calls.size)
    }

    @Test
    fun `debounces search so intermediate keystrokes never hit the repository`() = runTest {
        val repository = FakeProductRepository(total = 100)
        val viewModel = viewModel(repository)
        advanceUntilIdle()
        repository.calls.clear()

        viewModel.onQueryChange("p")
        advanceTimeBy(100)
        viewModel.onQueryChange("ph")
        advanceTimeBy(100)
        viewModel.onQueryChange("phone")
        advanceUntilIdle()

        assertEquals(listOf("phone"), repository.calls.map { it.query })
    }

    @Test
    fun `pages with the query the visible results were loaded with`() = runTest {
        val repository = FakeProductRepository(total = 100)
        val viewModel = viewModel(repository)
        advanceUntilIdle()

        viewModel.onQueryChange("phone")
        advanceUntilIdle()
        repository.calls.clear()

        // A new keystroke lands, but its debounce has not elapsed yet.
        viewModel.onQueryChange("phones")
        viewModel.onLoadMore()
        advanceUntilIdle()

        // Paging must continue "phone" — the query behind the products on
        // screen — not the half-typed "phones".
        assertEquals("phone", repository.calls.first { it.skip > 0 }.query)
    }

    @Test
    fun `surfaces a failure and recovers on retry`() = runTest {
        val repository = FakeProductRepository(total = 100)
        repository.error = CatalogError.NoConnection

        val viewModel = viewModel(repository)
        advanceUntilIdle()

        assertEquals(ErrorMessage.Network, viewModel.uiState.value.error)
        assertTrue(viewModel.uiState.value.products.isEmpty())

        repository.error = null
        viewModel.onRetry()
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.error)
        assertEquals(20, viewModel.uiState.value.products.size)
    }

    @Test
    fun `ignores a second load-more issued before the first one starts`() = runTest {
        val repository = FakeProductRepository(total = 100)
        // A dispatcher that queues rather than running eagerly, so both calls
        // land before either coroutine body executes — what happens on a real
        // main looper when two scroll callbacks fire in the same frame.
        val viewModel = ProductsViewModel(
            getProducts = GetProductsUseCase(repository),
            toggleFavorite = ToggleFavoriteProductUseCase(favoriteProductsRepository),
            getFavoriteProducts = GetFavoriteProductsUseCase(favoriteProductsRepository),
            dispatcherProvider = TestDispatcherProvider(StandardTestDispatcher(testScheduler)),
            productFormatter = productFormatter,
        )
        advanceUntilIdle()

        viewModel.onLoadMore()
        viewModel.onLoadMore()
        advanceUntilIdle()

        assertEquals(1, repository.calls.count { it.skip == 20 })
        assertEquals(40, viewModel.uiState.value.products.size)
    }


    @Test
    fun `surfaces a load-more failure while keeping the loaded products`() = runTest {
        val repository = FakeProductRepository(total = 100)
        val viewModel = viewModel(repository)
        advanceUntilIdle()

        repository.error = CatalogError.NoConnection
        viewModel.onLoadMore()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(ErrorMessage.Network, state.error)
        assertFalse(state.isLoadingMore)
        // The page already on screen must survive a failed append.
        assertEquals(20, state.products.size)
    }

    @Test
    fun `recovers paging after a load-more failure`() = runTest {
        val repository = FakeProductRepository(total = 100)
        val viewModel = viewModel(repository)
        advanceUntilIdle()

        repository.error = CatalogError.NoConnection
        viewModel.onLoadMore()
        advanceUntilIdle()

        repository.error = null
        viewModel.onLoadMore()
        advanceUntilIdle()

        assertEquals(40, viewModel.uiState.value.products.size)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `keeps paging usable after an in-flight page request is cancelled`() = runTest {
        val repository = FakeProductRepository(total = 100)
        val viewModel = ProductsViewModel(
            getProducts = GetProductsUseCase(repository),
            toggleFavorite = ToggleFavoriteProductUseCase(favoriteProductsRepository),
            getFavoriteProducts = GetFavoriteProductsUseCase(favoriteProductsRepository),
            dispatcherProvider = TestDispatcherProvider(StandardTestDispatcher(testScheduler)),
            productFormatter = productFormatter,
        )
        advanceUntilIdle()
        repository.calls.clear()

        viewModel.onLoadMore()
        // A new search cancels the in-flight page request.
        viewModel.onQueryChange("phone")
        advanceUntilIdle()

        // Paging must still work afterwards rather than being stranded.
        viewModel.onLoadMore()
        advanceUntilIdle()

        assertTrue(repository.calls.any { it.skip > 0 }, "paging stranded after cancellation")
    }

    @Test
    fun `reflects favorites from the repository`() = runTest {
        val repository = FakeProductRepository(total = 100)
        val viewModel = viewModel(repository)
        advanceUntilIdle()

        viewModel.onToggleFavorite(5)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.products.single { it.id == 5 }.isFavorite)
    }

    @Test
    fun `onClearQuery resets the query and reloads the default first page immediately`() = runTest {
        val repository = FakeProductRepository(total = 100)
        val viewModel = viewModel(repository)
        advanceUntilIdle()

        viewModel.onQueryChange("phone")
        advanceUntilIdle()
        repository.calls.clear()

        viewModel.onClearQuery()
        advanceUntilIdle()

        assertEquals("", viewModel.uiState.value.query)
        assertEquals(listOf(""), repository.calls.map { it.query })
    }
}
