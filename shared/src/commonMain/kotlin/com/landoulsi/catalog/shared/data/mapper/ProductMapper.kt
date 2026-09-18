package com.landoulsi.catalog.shared.data.mapper

import com.landoulsi.catalog.shared.data.remote.dto.ProductDto
import com.landoulsi.catalog.shared.data.remote.dto.ProductListResponseDto
import com.landoulsi.catalog.shared.domain.model.Product
import com.landoulsi.catalog.shared.domain.model.ProductPage

/** DTO -> domain conversions for [Product]. */
interface ProductMapper {
    fun toDomain(dto: ProductDto): Product
    fun toDomain(response: ProductListResponseDto): ProductPage
}

class ProductMapperImpl : ProductMapper {

    override fun toDomain(dto: ProductDto): Product = Product(
        id = dto.id,
        title = dto.title,
        description = dto.description,
        price = dto.price,
        discountPercentage = dto.discountPercentage,
        rating = dto.rating,
        stock = dto.stock,
        brand = dto.brand.orEmpty(),
        category = dto.category,
        thumbnail = dto.thumbnail,
        // Guarantee the details screen always has at least one image to show.
        images = dto.images.filter { it.isNotBlank() }
            .ifEmpty { listOf(dto.thumbnail).filter { it.isNotBlank() } },
    )

    override fun toDomain(response: ProductListResponseDto): ProductPage = ProductPage(
        products = response.products.map { toDomain(it) },
        total = response.total,
        skip = response.skip,
        limit = response.limit,
    )
}
