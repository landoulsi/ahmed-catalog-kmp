package com.landoulsi.catalog.ui.common

import com.landoulsi.catalog.R
import com.landoulsi.catalog.shared.presentation.common.ErrorMessage

/**
 * Resolves a shared [ErrorMessage] to a [UiText].
 *
 * Exhaustive with no `else`, so adding a variant in shared code becomes a
 * compile error here rather than a silent fallback to a generic message.
 * Returning [UiText] rather than a bare `@StringRes` int means a variant that
 * later needs a formatting argument (e.g. a retry countdown) is a call-site
 * change here only, not a signature change at every caller.
 */
fun ErrorMessage.toUiText(): UiText = when (this) {
    ErrorMessage.Network -> UiText.Res(R.string.error_network)
    ErrorMessage.Timeout -> UiText.Res(R.string.error_timeout)
    ErrorMessage.Server -> UiText.Res(R.string.error_server)
    ErrorMessage.Unknown -> UiText.Res(R.string.error_generic)
}
