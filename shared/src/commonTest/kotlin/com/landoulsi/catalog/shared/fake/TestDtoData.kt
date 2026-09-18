package com.landoulsi.catalog.shared.fake

import com.landoulsi.catalog.shared.data.remote.dto.ProductDto
import com.landoulsi.catalog.shared.data.remote.dto.ProductListResponseDto

/** Minimal valid [ProductDto] for tests; override only what a test cares about. */
fun testProductDto(
    id: Int = 1,
    title: String = "Product $id",
    price: Double = 10.0,
    discountPercentage: Double = 0.0,
    stock: Int = 5,
): ProductDto = ProductDto(
    id = id,
    title = title,
    description = "Description $id",
    price = price,
    discountPercentage = discountPercentage,
    rating = 4.5,
    stock = stock,
    brand = "Brand",
    category = "category",
    thumbnail = "https://example.com/$id.png",
    images = listOf("https://example.com/$id.png"),
)

/** [ProductListResponseDto] envelope wrapping [products], with paging defaults. */
fun testProductListResponseDto(
    products: List<ProductDto> = listOf(testProductDto()),
    total: Int = products.size,
    skip: Int = 0,
    limit: Int = products.size,
): ProductListResponseDto = ProductListResponseDto(
    products = products,
    total = total,
    skip = skip,
    limit = limit,
)
