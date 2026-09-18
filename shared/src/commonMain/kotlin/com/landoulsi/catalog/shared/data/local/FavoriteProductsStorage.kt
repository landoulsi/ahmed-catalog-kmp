package com.landoulsi.catalog.shared.data.local

import com.landoulsi.catalog.shared.domain.model.Product

interface FavoriteProductsStorage {
    /** Returns an empty list when nothing has been persisted yet. */
    fun read(): List<Product>

    /** Replaces the persisted set with [products]. */
    fun write(products: List<Product>)
}
