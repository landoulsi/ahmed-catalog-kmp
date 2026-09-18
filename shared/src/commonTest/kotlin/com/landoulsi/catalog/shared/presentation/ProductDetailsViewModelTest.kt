package com.landoulsi.catalog.shared.presentation

import com.landoulsi.catalog.shared.core.CatalogError
import com.landoulsi.catalog.shared.core.DataResult
import com.landoulsi.catalog.shared.domain.usecase.GetProductDetailsUseCase
import com.landoulsi.catalog.shared.domain.usecase.IsFavoriteProductUseCase
import com.landoulsi.catalog.shared.domain.usecase.ToggleFavoriteProductUseCase
import com.landoulsi.catalog.shared.fake.FakeFavoriteProductsRepository
import com.landoulsi.catalog.shared.fake.FakeProductFormatter
import com.landoulsi.catalog.shared.fake.FakeProductRepository
import com.landoulsi.catalog.shared.fake.TestDispatcherProvider
import com.landoulsi.catalog.shared.fake.testProduct
import com.landoulsi.catalog.shared.presentation.common.ErrorMessage
import com.landoulsi.catalog.shared.presentation.details.ProductDetailsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ProductDetailsViewModelTest {

    private val favoriteProductsRepository = FakeFavoriteProductsRepository()
    private val productFormatter = FakeProductFormatter()

    private fun TestScope.viewModel(productId: Int, repository: FakeProductRepository) =
        ProductDetailsViewModel(
            productId = productId,
            getProductDetails = GetProductDetailsUseCase(repository),
            toggleFavorite = ToggleFavoriteProductUseCase(favoriteProductsRepository),
            isFavorite = IsFavoriteProductUseCase(favoriteProductsRepository),
            dispatcherProvider = TestDispatcherProvider(UnconfinedTestDispatcher(testScheduler)),
            productFormatter = productFormatter,
        )

    @Test
    fun `loads the requested product on creation`() = runTest {
        val repository = FakeProductRepository()
        repository.detailsResult = DataResult.Success(
            testProduct(id = 42, title = "Laptop", price = 100.0, discountPercentage = 10.0, rating = 4.5)
        )

        val viewModel = viewModel(productId = 42, repository = repository)
        advanceUntilIdle()

        val content = viewModel.uiState.value.content
        assertEquals("Laptop", content?.title)
        assertEquals("90.00", content?.formattedPrice)
        assertEquals("10", content?.formattedDiscount)
        assertEquals("4.5", content?.formattedRating)
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `exposes a failure when the product cannot be loaded`() = runTest {
        val repository = FakeProductRepository()
        repository.detailsResult = DataResult.Failure(CatalogError.Server(code = 404))

        val viewModel = viewModel(productId = 1, repository = repository)
        advanceUntilIdle()

        assertEquals(ErrorMessage.Server, viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `toggling updates favorite state from the repository`() = runTest {
        val repository = FakeProductRepository()
        repository.detailsResult = DataResult.Success(testProduct(id = 7))
        val viewModel = viewModel(productId = 7, repository = repository)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isFavorite)

        viewModel.onToggleFavorite()
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isFavorite)

        viewModel.onToggleFavorite()
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isFavorite)
    }

    @Test
    fun `ignores a favorite toggle before the product has loaded`() = runTest {
        val repository = FakeProductRepository()
        repository.detailsResult = DataResult.Failure(CatalogError.NoConnection)
        val viewModel = viewModel(productId = 3, repository = repository)
        advanceUntilIdle()

        viewModel.onToggleFavorite()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isFavorite)
    }
}
