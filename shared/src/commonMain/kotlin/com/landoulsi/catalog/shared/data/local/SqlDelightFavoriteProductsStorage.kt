package com.landoulsi.catalog.shared.data.local

import com.landoulsi.catalog.shared.cache.FavoriteProduct
import com.landoulsi.catalog.shared.cache.FavoritesDatabase
import com.landoulsi.catalog.shared.domain.model.Product

/**
 * [FavoriteProductsStorage] backed by SQLDelight SQLite database.
 */
class SqlDelightFavoriteProductsStorage(
    private val database: FavoritesDatabase,
) : FavoriteProductsStorage {

    private val favoriteQueries = database.favoriteProductQueries

    override fun read(): List<Product> {
        val stored = favoriteQueries.selectAllFavorites().executeAsList()
        if (stored.isNotEmpty()) {
            return stored.map { it.toProduct() }
        }
        return emptyList()
    }

    override fun write(products: List<Product>) {
        database.transaction {
            favoriteQueries.clearAllFavorites()
            val baseTimestamp = products.size.toLong()
            products.forEachIndexed { index, product ->
                favoriteQueries.insertFavorite(
                    id = product.id.toLong(),
                    title = product.title,
                    description = product.description,
                    price = product.price,
                    discountPercentage = product.discountPercentage,
                    rating = product.rating,
                    stock = product.stock.toLong(),
                    brand = product.brand,
                    category = product.category,
                    thumbnail = product.thumbnail,
                    images = product.images.joinToString("\n"),
                    createdAt = baseTimestamp - index,
                )
            }
        }
    }

    private fun FavoriteProduct.toProduct(): Product = Product(
        id = id.toInt(),
        title = title,
        description = description,
        price = price,
        discountPercentage = discountPercentage,
        rating = rating,
        stock = stock.toInt(),
        brand = brand,
        category = category,
        thumbnail = thumbnail,
        images = images.split("\n").filter { it.isNotBlank() },
    )
}
