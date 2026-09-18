package com.landoulsi.catalog.shared.domain

import com.landoulsi.catalog.shared.fake.testProduct
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ProductTest {

    @Test
    fun `applies the discount and truncates to whole cents`() {
        val product = testProduct(price = 199.99, discountPercentage = 10.0)

        // 199.99 * 0.90 = 179.991 -> 179.99
        assertEquals(179.99, product.discountedPrice)
    }

    @Test
    fun `rounds to the nearest cent rather than truncating`() {
        // 1.01 * 0.99 = 0.9999 in IEEE-754; truncating drops a cent to 0.99.
        assertEquals(1.0, testProduct(price = 1.01, discountPercentage = 1.0).discountedPrice)
    }

    @Test
    fun `leaves the price untouched when there is no discount`() {
        val product = testProduct(price = 42.5, discountPercentage = 0.0)

        assertEquals(42.5, product.discountedPrice)
    }

    @Test
    fun `derives stock availability from the stock count`() {
        assertTrue(testProduct(stock = 1).isInStock)
        assertFalse(testProduct(stock = 0).isInStock)
    }
}
