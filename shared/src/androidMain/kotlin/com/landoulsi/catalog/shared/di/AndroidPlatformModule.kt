package com.landoulsi.catalog.shared.di

import android.content.Context
import android.content.pm.ApplicationInfo
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.landoulsi.catalog.shared.cache.FavoritesDatabase
import com.landoulsi.catalog.shared.presentation.common.ProductFormatter
import com.landoulsi.catalog.shared.presentation.common.ProductFormatterImpl
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.logging.LogLevel
import org.koin.dsl.module

/** Android-only bindings — each needs a concrete platform type (Context, OkHttp, java.util.Locale). */
val androidPlatformModule = module {
    single { PlatformHttpEngine(HttpClient(OkHttp)) }
    single<SqlDriver> {
        val context: Context = get()
        AndroidSqliteDriver(FavoritesDatabase.Schema, context, FAVORITES_DATABASE_NAME)
    }
    single<ProductFormatter> { ProductFormatterImpl(currencyProvider = get()) }
    single {
        val context: Context = get()
        val isDebuggable = context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
        HttpLogLevel(if (isDebuggable) LogLevel.ALL else LogLevel.NONE)
    }
}
