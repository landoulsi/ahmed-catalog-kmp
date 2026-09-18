package com.landoulsi.catalog.ui.common

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

/**
 * A string the UI can render, either a plain literal or an Android string
 * resource with optional formatting args.
 *
 * Wrapping the resource id (rather than resolving it eagerly) lets a mapper
 * attach `formatArgs` later without changing its return type at every caller.
 */
sealed interface UiText {
    data class Res(@StringRes val id: Int, val formatArgs: List<Any> = emptyList()) : UiText
    data class Plain(val value: String) : UiText
}

@Composable
fun UiText.resolve(): String = when (this) {
    is UiText.Res -> stringResource(id, *formatArgs.toTypedArray())
    is UiText.Plain -> value
}
