package com.landoulsi.catalog.shared.core

/**
 * Result of an operation that can fail in a way the UI is expected to handle.
 *
 * Domain and presentation code pattern-match on this instead of catching
 * exceptions, so error handling stays explicit at every layer boundary.
 */
sealed interface DataResult<out T> {
    data class Success<out T>(val data: T) : DataResult<T>
    data class Failure(val error: CatalogError) : DataResult<Nothing>
}

/** Errors the catalog can surface, independent of any networking library. */
sealed interface CatalogError {
    /** No connectivity or DNS failure — retrying likely won't help until the connection is back. */
    data object NoConnection : CatalogError

    /** The request took too long — the connection may be present but slow or unstable. */
    data object Timeout : CatalogError

    /** Server answered with a non-success status. */
    data class Server(val code: Int) : CatalogError

    /** Response body could not be parsed into the expected shape. */
    data object Serialization : CatalogError

    /** Anything not covered above. */
    data class Unknown(val message: String?) : CatalogError
}

inline fun <T, R> DataResult<T>.map(transform: (T) -> R): DataResult<R> = when (this) {
    is DataResult.Success -> DataResult.Success(transform(data))
    is DataResult.Failure -> this
}
