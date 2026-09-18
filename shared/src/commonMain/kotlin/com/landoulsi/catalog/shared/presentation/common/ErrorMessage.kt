package com.landoulsi.catalog.shared.presentation.common

import com.landoulsi.catalog.shared.core.CatalogError

/**
 * A message the UI should show, named rather than pre-formatted.
 *
 * The shared layer decides *which* message applies; each platform decides how
 * to render it — Android resolves these against `strings.xml`, iOS against its
 * own localized strings. Keeping raw text out of shared code is what makes the
 * ViewModels genuinely reusable.
 */
enum class ErrorMessage {
    Network,
    Timeout,
    Server,
    Unknown,
}

fun CatalogError.toErrorMessage(): ErrorMessage = when (this) {
    CatalogError.NoConnection -> ErrorMessage.Network
    CatalogError.Timeout -> ErrorMessage.Timeout
    is CatalogError.Server -> ErrorMessage.Server
    CatalogError.Serialization -> ErrorMessage.Unknown
    is CatalogError.Unknown -> ErrorMessage.Unknown
}
