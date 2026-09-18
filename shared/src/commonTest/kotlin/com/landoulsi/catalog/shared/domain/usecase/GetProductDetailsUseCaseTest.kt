package com.landoulsi.catalog.shared.domain.usecase

import com.landoulsi.catalog.shared.core.CatalogError
import com.landoulsi.catalog.shared.core.DataResult
import com.landoulsi.catalog.shared.fake.RecordingProductRepository
import com.landoulsi.catalog.shared.fake.testProduct
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class GetProductDetailsUseCaseTest {

    @Test
    fun `forwards id and returns success unchanged`() = runTest {
        val expected = DataResult.Success(testProduct(id = 42))
        val repository = RecordingProductRepository(detailsResult = expected)

        val actual = GetProductDetailsUseCase(repository)(id = 42)

        assertEquals(listOf(42), repository.detailCalls)
        assertSame(expected, actual)
    }

    @Test
    fun `returns repository failure unchanged`() = runTest {
        val expected = DataResult.Failure(CatalogError.NoConnection)
        val repository = RecordingProductRepository(detailsResult = expected)

        val actual = GetProductDetailsUseCase(repository)(id = 99)

        assertEquals(listOf(99), repository.detailCalls)
        assertSame(expected, actual)
    }
}
