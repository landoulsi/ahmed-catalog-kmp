package com.landoulsi.catalog.shared.fake

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.landoulsi.catalog.shared.cache.FavoritesDatabase

actual fun createTestDatabase(): FavoritesDatabase {
    val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    FavoritesDatabase.Schema.create(driver)
    return FavoritesDatabase(driver)
}
