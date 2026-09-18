package com.landoulsi.catalog.shared.core

import timber.log.Timber

private const val TAG = "ProductCatalog"

actual fun platformLogger(): Logger = object : Logger {
    override fun error(throwable: Throwable, message: String) {
        Timber.tag(TAG).e(throwable, message)
    }

    override fun debug(message: String) {
        // Split on newlines so a multi-line body (or one long JSON blob) logs
        // as separate entries — logcat truncates/garbles a single huge line.
        message.lineSequence().forEach { line ->
            if (line.isNotEmpty()) Timber.tag(TAG).d(line)
        }
    }
}
