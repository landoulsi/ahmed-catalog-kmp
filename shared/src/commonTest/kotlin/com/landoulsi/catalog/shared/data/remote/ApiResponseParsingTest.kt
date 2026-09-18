package com.landoulsi.catalog.shared.data.remote

import com.landoulsi.catalog.shared.data.remote.dto.ProductDto
import com.landoulsi.catalog.shared.data.remote.dto.ProductListResponseDto
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Parses raw JSON shaped like real DummyJSON responses through the same [Json]
 * configuration [HttpClientFactory] installs, so a change to that config (or to
 * a DTO's `@SerialName`) that breaks parsing fails here rather than only
 * surfacing as a runtime deserialization error against the live API.
 */
class ApiResponseParsingTest {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
    }

    @Test
    fun `parses a single product with every field present`() {
        val body = """
            {
              "id": 1,
              "title": "Essence Mascara Lash Princess",
              "description": "The Essence Mascara Lash Princess is a popular mascara.",
              "price": 9.99,
              "discountPercentage": 10.48,
              "rating": 2.56,
              "stock": 99,
              "brand": "Essence",
              "category": "beauty",
              "thumbnail": "https://cdn.dummyjson.com/thumbnail.png",
              "images": ["https://cdn.dummyjson.com/1.png", "https://cdn.dummyjson.com/2.png"]
            }
        """.trimIndent()

        val dto = json.decodeFromString<ProductDto>(body)

        assertEquals(1, dto.id)
        assertEquals("Essence Mascara Lash Princess", dto.title)
        assertEquals(9.99, dto.price)
        assertEquals(10.48, dto.discountPercentage)
        assertEquals(2.56, dto.rating)
        assertEquals(99, dto.stock)
        assertEquals("Essence", dto.brand)
        assertEquals("beauty", dto.category)
        assertEquals(2, dto.images.size)
    }

    @Test
    fun `rejects a product with missing required fields`() {
        val body = """{ "id": 42 }"""

        assertFailsWith<Exception> {
            json.decodeFromString<ProductDto>(body)
        }
    }

    @Test
    fun `parses a product with no brand field as DummyJSON groceries category sends`() {
        val body = """
            {
              "id": 16,
              "title": "Apple",
              "description": "Fresh red apple",
              "price": 1.99,
              "discountPercentage": 0.0,
              "rating": 4.0,
              "stock": 50,
              "category": "groceries",
              "thumbnail": "https://cdn.dummyjson.com/thumbnail.png",
              "images": ["https://cdn.dummyjson.com/1.png"]
            }
        """.trimIndent()

        val dto = json.decodeFromString<ProductDto>(body)

        assertEquals(16, dto.id)
        assertEquals(null, dto.brand)
    }

    @Test
    fun `ignores fields the dto does not model`() {
        // DummyJSON products carry many more fields (tags, dimensions, reviews,
        // meta...) than this app needs; parsing must not fail on their presence.
        val body = """
            {
              "id": 1,
              "title": "Phone",
              "description": "A phone",
              "price": 100.0,
              "discountPercentage": 0.0,
              "rating": 4.0,
              "stock": 10,
              "brand": "Brand",
              "category": "phones",
              "thumbnail": "https://example.com/thumb.png",
              "images": ["https://example.com/1.png"],
              "tags": ["beauty", "mascara"],
              "weight": 2,
              "dimensions": { "width": 23.17, "height": 14.43, "depth": 28.01 },
              "warrantyInformation": "1 month warranty",
              "meta": { "barcode": "9164035109868" }
            }
        """.trimIndent()

        val dto = json.decodeFromString<ProductDto>(body)

        assertEquals(1, dto.id)
        assertEquals("Phone", dto.title)
    }

    @Test
    fun `parses the list envelope with its paging cursor`() {
        val body = """
            {
              "products": [
                {
                  "id": 1, "title": "Essence Mascara Lash Princess", "description": "Mascara",
                  "price": 9.99, "discountPercentage": 7.17, "rating": 4.94, "stock": 5,
                  "brand": "Essence", "category": "beauty", "thumbnail": "thumb1", "images": ["image1"]
                },
                {
                  "id": 2, "title": "Eyeshadow Palette with Mirror", "description": "Eyeshadow",
                  "price": 19.99, "discountPercentage": 5.0, "rating": 4.5, "stock": 10,
                  "brand": "Brand", "category": "beauty", "thumbnail": "thumb2", "images": ["image2"]
                }
              ],
              "total": 194,
              "skip": 0,
              "limit": 2
            }
        """.trimIndent()

        val response = json.decodeFromString<ProductListResponseDto>(body)

        assertEquals(listOf(1, 2), response.products.map { it.id })
        assertEquals(194, response.total)
        assertEquals(0, response.skip)
        assertEquals(2, response.limit)
    }

    @Test
    fun `parses an empty list envelope for a query with no matches`() {
        val body = """{ "products": [], "total": 0, "skip": 0, "limit": 0 }"""

        val response = json.decodeFromString<ProductListResponseDto>(body)

        assertTrue(response.products.isEmpty())
        assertEquals(0, response.total)
    }

    @Test
    fun `parses the envelope when it is missing paging fields`() {
        val body = """{ "products": [] }"""

        val response = json.decodeFromString<ProductListResponseDto>(body)

        assertTrue(response.products.isEmpty())
        assertEquals(0, response.total)
        assertEquals(0, response.skip)
        assertEquals(0, response.limit)
    }
}
