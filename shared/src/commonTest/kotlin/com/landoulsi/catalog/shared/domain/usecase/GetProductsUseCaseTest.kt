package com.landoulsi.catalog.shared.domain.usecase

import com.landoulsi.catalog.shared.core.CatalogError
import com.landoulsi.catalog.shared.core.DataResult
import com.landoulsi.catalog.shared.domain.model.ProductPage
import com.landoulsi.catalog.shared.fake.ProductCall
import com.landoulsi.catalog.shared.fake.RecordingProductRepository
import com.landoulsi.catalog.shared.fake.testProduct
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class GetProductsUseCaseTest {

    private val repository = RecordingProductRepository()
    private val getProducts = GetProductsUseCase(repository)

    @Test
    fun `trims the query and uses default paging`() = runTest {
        getProducts(query = "  phone  ")

        assertEquals(
            ProductCall(query = "phone", skip = 0, limit = GetProductsUseCase.DEFAULT_PAGE_SIZE),
            repository.productCalls.single(),
        )
    }

    @Test
    fun `forwards custom paging arguments`() = runTest {
        getProducts(query = "laptop", skip = 40, limit = 10)

        assertEquals(
            ProductCall(query = "laptop", skip = 40, limit = 10),
            repository.productCalls.single(),
        )
    }

    @Test
    fun `returns repository success unchanged`() = runTest {
        val page = ProductPage(
            products = listOf(testProduct(id = 7)),
            total = 1,
            skip = 0,
            limit = 20,
        )
        val expected = DataResult.Success(page)
        val repository = RecordingProductRepository(productsResult = expected)

        val actual = GetProductsUseCase(repository)()

        assertSame(expected, actual)
    }

    @Test
    fun `returns repository failure unchanged`() = runTest {
        val expected = DataResult.Failure(CatalogError.Server(code = 503))
        val repository = RecordingProductRepository(productsResult = expected)

        val actual = GetProductsUseCase(repository)()

        assertSame(expected, actual)
    }
}
