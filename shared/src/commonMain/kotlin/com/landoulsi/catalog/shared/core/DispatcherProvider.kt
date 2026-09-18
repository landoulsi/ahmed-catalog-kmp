package com.landoulsi.catalog.shared.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

interface DispatcherProvider {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
}

/** Per-platform blocking-work dispatcher: JVM's `Dispatchers.IO`, Native's `Dispatchers.Default`. */
expect fun ioDispatcher(): CoroutineDispatcher

class DispatcherProviderImpl : DispatcherProvider {
    override val main: CoroutineDispatcher get() = Dispatchers.Main
    override val io: CoroutineDispatcher get() = ioDispatcher()
}
