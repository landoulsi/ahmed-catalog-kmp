package com.landoulsi.catalog.shared.di

import io.ktor.client.plugins.logging.LogLevel

/** Whether to log full HTTP requests/responses; platform modules decide this from build debuggability. */
data class HttpLogLevel(val value: LogLevel)