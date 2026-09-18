package com.landoulsi.catalog.shared.fake

import app.cash.sqldelight.driver.native.inMemoryDriver
import com.landoulsi.catalog.shared.cache.FavoritesDatabase

actual fun createTestDatabase(): FavoritesDatabase {
    val driver = inMemoryDriver(FavoritesDatabase.Schema)
    return FavoritesDatabase(driver)
}
