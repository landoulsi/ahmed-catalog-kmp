package com.landoulsi.catalog.shared.domain.model

/**
 * One page of products plus the cursor state needed to request the next one.
 */
data class ProductPage(
    val products: List<Product>,
    val total: Int,
    val skip: Int,
    val limit: Int,
) {
    /** True when the API holds more products beyond the ones in this page. */
    val hasMore: Boolean get() = skip + products.size < total

    /** `skip` value that should be used to fetch the page after this one. */
    val nextSkip: Int get() = skip + products.size
}
