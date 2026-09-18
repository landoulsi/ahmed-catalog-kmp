package com.landoulsi.catalog.shared.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.landoulsi.catalog.shared.cache.FavoritesDatabase
import com.landoulsi.catalog.shared.presentation.common.ProductFormatter
import com.landoulsi.catalog.shared.presentation.common.ProductFormatterImpl
import com.landoulsi.catalog.shared.presentation.details.ProductDetailsViewModel
import com.landoulsi.catalog.shared.presentation.favorites.FavoriteProductsViewModel
import com.landoulsi.catalog.shared.presentation.products.ProductsViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.logging.LogLevel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val iosPlatformModule = module {
    single { PlatformHttpEngine(HttpClient(Darwin)) }
    single<SqlDriver> {
        NativeSqliteDriver(FavoritesDatabase.Schema, FAVORITES_DATABASE_NAME)
    }
    single<ProductFormatter> { ProductFormatterImpl(currencyProvider = get()) }
    // No release-build story on iOS in this project yet, so this always logs;
    // gate it the same way androidPlatformModule does before shipping an iOS app.
    single { HttpLogLevel(LogLevel.ALL) }
}

/**
 * Accessor for iOS / Swift to fetch ViewModels from the Koin container.
 */
class IosKoinHelper : KoinComponent {
    fun productsViewModel(): ProductsViewModel = get()
    fun favoritesViewModel(): FavoriteProductsViewModel = get()
    fun productDetailsViewModel(productId: Int): ProductDetailsViewModel =
        get(parameters = { parametersOf(productId) })
}

/**
 * Initializes Koin for iOS.
 *
 * Called once from Swift (e.g. AppDelegate or @main App init).
 */
fun initKoinIos(): IosKoinHelper {
    startKoin {
        modules(commonModules + iosPlatformModule)
    }
    return IosKoinHelper()
}
