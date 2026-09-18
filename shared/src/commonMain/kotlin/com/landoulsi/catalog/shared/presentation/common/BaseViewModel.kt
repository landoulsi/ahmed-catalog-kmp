package com.landoulsi.catalog.shared.presentation.common

import androidx.lifecycle.ViewModel
import com.landoulsi.catalog.shared.core.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.StateFlow

/**
 * Base for the shared ViewModels.
 *
 * Extends the multiplatform [ViewModel] so Android's ViewModelStore can own the
 * instance across configuration changes, but runs work on [scope] — built from
 * the injected [DispatcherProvider] — rather than `viewModelScope`. That keeps
 * the dispatcher an explicit dependency, so tests supply a deterministic one
 * instead of overriding a global.
 *
 * [SupervisorJob] means one failed child coroutine does not cancel the others.
 */
abstract class BaseViewModel<S : UiState>(
    dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    abstract val uiState: StateFlow<S>

    protected val scope: CoroutineScope =
        CoroutineScope(SupervisorJob() + dispatcherProvider.main)

    /**
     * Cancels in-flight work.
     *
     * Android calls this via the ViewModelStore; iOS callers invoke it when
     * tearing the screen down (see the README).
     */
    public override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }
}
