package com.landoulsi.catalog.shared.fake

import com.landoulsi.catalog.shared.core.CatalogError
import com.landoulsi.catalog.shared.core.DataResult
import com.landoulsi.catalog.shared.domain.model.Product
import com.landoulsi.catalog.shared.domain.model.ProductPage
import com.landoulsi.catalog.shared.domain.repository.ProductRepository

/** Records the arguments it was called with, so tests can assert on paging. */
class FakeProductRepository(
    private val total: Int = 100,
    private val pageFactory: (query: String, skip: Int, limit: Int) -> List<Product> =
        { _, skip, limit -> List(limit) { index -> testProduct(id = skip + index) } },
) : ProductRepository {

    data class Call(val query: String, val skip: Int, val limit: Int)

    val calls = mutableListOf<Call>()

    var error: CatalogError? = null

    var detailsResult: DataResult<Product> = DataResult.Success(testProduct(id = 1))

    override suspend fun getProducts(
        query: String,
        skip: Int,
        limit: Int,
    ): DataResult<ProductPage> {
        calls += Call(query, skip, limit)
        error?.let { return DataResult.Failure(it) }

        val products = pageFactory(query, skip, limit)
        return DataResult.Success(
            ProductPage(products = products, total = total, skip = skip, limit = limit)
        )
    }

    override suspend fun getProduct(id: Int): DataResult<Product> = detailsResult
}

/** Minimal valid [Product] for tests; override only what a test cares about. */
fun testProduct(
    id: Int = 1,
    title: String = "Product $id",
    price: Double = 10.0,
    discountPercentage: Double = 0.0,
    rating: Double = 4.5,
    stock: Int = 5,
): Product = Product(
    id = id,
    title = title,
    description = "Description $id",
    price = price,
    discountPercentage = discountPercentage,
    rating = rating,
    stock = stock,
    brand = "Brand",
    category = "category",
    thumbnail = "https://example.com/$id.png",
    images = listOf("https://example.com/$id.png"),
)
