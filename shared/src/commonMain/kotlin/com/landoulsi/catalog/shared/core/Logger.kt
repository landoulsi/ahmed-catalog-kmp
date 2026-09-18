package com.landoulsi.catalog.shared.core

interface Logger {
    fun error(throwable: Throwable, message: String)
    fun debug(message: String)
}

/** Per-platform logging backend: Timber on Android, a plain println on iOS. */
expect fun platformLogger(): Logger

class LoggerImpl : Logger by platformLogger()
