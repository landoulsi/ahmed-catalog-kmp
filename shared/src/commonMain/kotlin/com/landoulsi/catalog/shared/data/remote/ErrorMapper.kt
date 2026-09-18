package com.landoulsi.catalog.shared.data.remote

import com.landoulsi.catalog.shared.core.CatalogError
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import io.ktor.serialization.ContentConvertException
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

/**
 * Translates transport-level exceptions into the domain's [CatalogError] set,
 * so nothing above the data layer needs to know Ktor exists.
 */
fun Throwable.toCatalogError(): CatalogError = when (this) {
    is ResponseException -> CatalogError.Server(response.status.value)
    is HttpRequestTimeoutException -> CatalogError.Timeout
    is IOException -> CatalogError.NoConnection
    // Ktor wraps deserialization failures, so unwrap before classifying.
    is ContentConvertException -> CatalogError.Serialization
    is SerializationException -> CatalogError.Serialization
    else -> CatalogError.Unknown(message)
}
