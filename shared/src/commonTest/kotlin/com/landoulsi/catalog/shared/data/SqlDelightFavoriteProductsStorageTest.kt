package com.landoulsi.catalog.shared.data

import com.landoulsi.catalog.shared.data.local.SqlDelightFavoriteProductsStorage
import com.landoulsi.catalog.shared.fake.createTestDatabase
import com.landoulsi.catalog.shared.fake.testProduct
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SqlDelightFavoriteProductsStorageTest {

    private lateinit var storage: SqlDelightFavoriteProductsStorage

    @BeforeTest
    fun setUp() {
        val database = createTestDatabase()
        storage = SqlDelightFavoriteProductsStorage(database)
    }

    @Test
    fun `read returns empty list when no favorites stored`() {
        assertTrue(storage.read().isEmpty())
    }

    @Test
    fun `write and read preserves stored products and order`() {
        val list = listOf(testProduct(id = 1), testProduct(id = 2), testProduct(id = 3))
        storage.write(list)

        val retrieved = storage.read()
        assertEquals(3, retrieved.size)
        assertEquals(listOf(1, 2, 3), retrieved.map { it.id })
    }

}
