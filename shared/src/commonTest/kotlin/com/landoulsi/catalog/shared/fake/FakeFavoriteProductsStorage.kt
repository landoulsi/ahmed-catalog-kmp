package com.landoulsi.catalog.shared.fake

import com.landoulsi.catalog.shared.data.local.FavoriteProductsStorage
import com.landoulsi.catalog.shared.domain.model.Product

/**
 * Hand-written rather than mocked: fakes work in `commonTest` on every target,
 * whereas mocking libraries are generally JVM-only.
 */
class FakeFavoriteProductsStorage(
    initial: List<Product> = emptyList(),
) : FavoriteProductsStorage {

    var stored: List<Product> = initial
        private set

    var writeCount = 0
        private set

    override fun read(): List<Product> = stored

    override fun write(products: List<Product>) {
        stored = products
        writeCount++
    }
}
