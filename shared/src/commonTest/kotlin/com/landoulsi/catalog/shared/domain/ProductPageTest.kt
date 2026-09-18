package com.landoulsi.catalog.shared.domain

import com.landoulsi.catalog.shared.domain.model.ProductPage
import com.landoulsi.catalog.shared.fake.testProduct
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ProductPageTest {

    @Test
    fun `reports more pages while products remain`() {
        val page = ProductPage(
            products = List(20) { testProduct(id = it) },
            total = 194,
            skip = 0,
            limit = 20,
        )

        assertTrue(page.hasMore)
        assertEquals(20, page.nextSkip)
    }

    @Test
    fun `reports no more pages once the last one is reached`() {
        // 194 total, 180 already consumed, this page returns the final 14.
        val page = ProductPage(
            products = List(14) { testProduct(id = 180 + it) },
            total = 194,
            skip = 180,
            limit = 20,
        )

        assertFalse(page.hasMore)
        assertEquals(194, page.nextSkip)
    }

    @Test
    fun `reports no more pages for an empty result`() {
        val page = ProductPage(products = emptyList(), total = 0, skip = 0, limit = 20)

        assertFalse(page.hasMore)
    }
}
