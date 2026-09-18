package com.landoulsi.catalog.shared.data

import com.landoulsi.catalog.shared.data.mapper.ProductMapperImpl
import com.landoulsi.catalog.shared.data.remote.dto.ProductDto
import com.landoulsi.catalog.shared.data.remote.dto.ProductListResponseDto
import com.landoulsi.catalog.shared.fake.testProductDto
import kotlin.test.Test
import kotlin.test.assertEquals

class ProductMapperTest {

    private val mapper = ProductMapperImpl()

    @Test
    fun `maps every field from a complete dto`() {
        val dto = ProductDto(
            id = 7,
            title = "Phone",
            description = "A phone",
            price = 199.99,
            discountPercentage = 10.0,
            rating = 4.2,
            stock = 3,
            brand = "Acme",
            category = "smartphones",
            thumbnail = "https://example.com/thumb.png",
            images = listOf("https://example.com/1.png", "https://example.com/2.png"),
        )

        val product = mapper.toDomain(dto)

        assertEquals(7, product.id)
        assertEquals("Phone", product.title)
        assertEquals("A phone", product.description)
        assertEquals(199.99, product.price)
        assertEquals(10.0, product.discountPercentage)
        assertEquals(4.2, product.rating)
        assertEquals(3, product.stock)
        assertEquals("Acme", product.brand)
        assertEquals("smartphones", product.category)
        assertEquals(2, product.images.size)
    }

    @Test
    fun `maps a missing brand to an empty string instead of failing`() {
        val dto = testProductDto().copy(brand = null)

        val product = mapper.toDomain(dto)

        assertEquals("", product.brand)
    }

    @Test
    fun `falls back to the thumbnail when the dto carries no images`() {
        val dto = testProductDto().copy(
            thumbnail = "https://example.com/thumb.png",
            images = emptyList(),
        )

        val product = mapper.toDomain(dto)

        assertEquals(listOf("https://example.com/thumb.png"), product.images)
    }

    @Test
    fun `maps the list envelope including its paging cursor`() {
        val response = ProductListResponseDto(
            products = listOf(testProductDto(id = 1), testProductDto(id = 2)),
            total = 194,
            skip = 20,
            limit = 2,
        )

        val page = mapper.toDomain(response)

        assertEquals(listOf(1, 2), page.products.map { it.id })
        assertEquals(194, page.total)
        assertEquals(20, page.skip)
        assertEquals(2, page.limit)
    }
}
