package com.landoulsi.catalog.shared.core

actual fun platformLogger(): Logger = object : Logger {
    override fun error(throwable: Throwable, message: String) {
        println("ERROR: $message\n${throwable.stackTraceToString()}")
    }

    override fun debug(message: String) {
        println("DEBUG: $message")
    }
}
