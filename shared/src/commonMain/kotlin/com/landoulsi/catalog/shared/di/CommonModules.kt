package com.landoulsi.catalog.shared.di

import com.landoulsi.catalog.shared.cache.FavoritesDatabase
import com.landoulsi.catalog.shared.core.DispatcherProvider
import com.landoulsi.catalog.shared.core.DispatcherProviderImpl
import com.landoulsi.catalog.shared.core.Logger
import com.landoulsi.catalog.shared.core.LoggerImpl
import com.landoulsi.catalog.shared.data.local.FavoriteProductsStorage
import com.landoulsi.catalog.shared.data.local.SqlDelightFavoriteProductsStorage
import com.landoulsi.catalog.shared.data.mapper.ProductMapper
import com.landoulsi.catalog.shared.data.mapper.ProductMapperImpl
import com.landoulsi.catalog.shared.data.remote.CatalogApi
import com.landoulsi.catalog.shared.data.remote.HttpClientFactory
import com.landoulsi.catalog.shared.data.repository.FavoriteProductsRepositoryImpl
import com.landoulsi.catalog.shared.data.repository.ProductRepositoryImpl
import com.landoulsi.catalog.shared.domain.repository.FavoriteProductsRepository
import com.landoulsi.catalog.shared.domain.repository.ProductRepository
import com.landoulsi.catalog.shared.domain.usecase.GetFavoriteProductsUseCase
import com.landoulsi.catalog.shared.domain.usecase.GetProductDetailsUseCase
import com.landoulsi.catalog.shared.domain.usecase.GetProductsUseCase
import com.landoulsi.catalog.shared.domain.usecase.IsFavoriteProductUseCase
import com.landoulsi.catalog.shared.domain.usecase.ToggleFavoriteProductUseCase
import com.landoulsi.catalog.shared.presentation.common.CurrencyProvider
import com.landoulsi.catalog.shared.presentation.details.ProductDetailsViewModel
import com.landoulsi.catalog.shared.presentation.favorites.FavoriteProductsViewModel
import com.landoulsi.catalog.shared.presentation.products.ProductsViewModel
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

const val FAVORITES_DATABASE_NAME = "favorites_products.db"

/** Not a fourth layer — bindings that need a platform-supplied dependency to construct (e.g. `HttpClient`). */
val sharedModule = module {
    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            explicitNulls = false
        }
    }
    single {
        HttpClientFactory.create(
            get<PlatformHttpEngine>().client,
            host = CatalogApi.HOST,
            logger = get(),
            json = get(),
            logLevel = get<HttpLogLevel>().value,
        )
    }
    single {
        FavoritesDatabase(get())
    }
}

/** Data-layer bindings — no platform dependency of their own, unlike [sharedModule]. */
val dataModule = module {
    single<DispatcherProvider> { DispatcherProviderImpl() }
    single<Logger> { LoggerImpl() }
    single<CurrencyProvider> { CurrencyProvider() }
    single { CatalogApi(get()) }
    single<FavoriteProductsStorage> { SqlDelightFavoriteProductsStorage(get()) }
    single<ProductMapper> { ProductMapperImpl() }
    single<ProductRepository> { ProductRepositoryImpl(get(), get(), get()) }
    single<FavoriteProductsRepository> { FavoriteProductsRepositoryImpl(get(), get()) }
}

/** Business logic, `factory { }` since use cases are cheap and stateless. No Ktor/Android/iOS imports here. */
val domainModule = module {
    factory { GetProductsUseCase(get()) }
    factory { GetProductDetailsUseCase(get()) }
    factory { GetFavoriteProductsUseCase(get()) }
    factory { IsFavoriteProductUseCase(get()) }
    factory { ToggleFavoriteProductUseCase(get()) }
}

/**
 * `viewModel { }`, not `factory`/`single` — ties resolution to the platform's
 * real `ViewModelStoreOwner`, so instances survive recomposition and
 * `onCleared()` fires at the right time. `factory` would lose state every
 * recomposition; `single` can't take per-screen params like `productId`.
 */
val presentationModule = module {
    viewModel { ProductsViewModel(get(), get(), get(), get(), get()) }
    viewModel { FavoriteProductsViewModel(get(), get(), get(), get()) }
    viewModel { (productId: Int) -> ProductDetailsViewModel(productId, get(), get(), get(), get(), get()) }
}

val commonModules = listOf(
    sharedModule,
    dataModule,
    domainModule,
    presentationModule,
)

