package com.landoulsi.catalog.shared.fake

import com.landoulsi.catalog.shared.core.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher

/**
 * Unconfined, so coroutines a ViewModel launches run eagerly and a test can
 * assert on state right after triggering an action.
 *
 * Pass the test's own scheduler to keep virtual time shared — the search-debounce
 * tests advance time explicitly.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TestDispatcherProvider(
    private val dispatcher: CoroutineDispatcher = UnconfinedTestDispatcher(),
) : DispatcherProvider {
    override val main: CoroutineDispatcher get() = dispatcher
    override val io: CoroutineDispatcher get() = dispatcher
}
