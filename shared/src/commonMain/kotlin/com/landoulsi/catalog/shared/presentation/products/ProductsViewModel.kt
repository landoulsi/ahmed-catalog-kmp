package com.landoulsi.catalog.shared.presentation.products

import com.landoulsi.catalog.shared.core.DataResult
import com.landoulsi.catalog.shared.core.DispatcherProvider
import com.landoulsi.catalog.shared.domain.model.Product
import com.landoulsi.catalog.shared.domain.usecase.GetFavoriteProductsUseCase
import com.landoulsi.catalog.shared.domain.usecase.GetProductsUseCase
import com.landoulsi.catalog.shared.domain.usecase.ToggleFavoriteProductUseCase
import com.landoulsi.catalog.shared.presentation.common.BaseViewModel
import com.landoulsi.catalog.shared.presentation.common.ProductFormatter
import com.landoulsi.catalog.shared.presentation.common.ProductUiState
import com.landoulsi.catalog.shared.presentation.common.toErrorMessage
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Drives the products list screen: paging, debounced search and favorite toggles.
 *
 * Exposes [uiState] as a [StateFlow] and accepts plain function calls, with no
 * Compose or Android types in its signature, so an iOS app can observe the same
 * instance (see the README for how).
 */
@OptIn(FlowPreview::class)
class ProductsViewModel(
    private val getProducts: GetProductsUseCase,
    private val toggleFavorite: ToggleFavoriteProductUseCase,
    getFavoriteProducts: GetFavoriteProductsUseCase,
    dispatcherProvider: DispatcherProvider,
    private val productFormatter: ProductFormatter,
) : BaseViewModel<ProductListUiState>(dispatcherProvider) {

    private val _uiState = MutableStateFlow(ProductListUiState())
    override val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()

    /** Raw query input; debounced before it reaches the network. */
    private val queryInput = MutableStateFlow("")

    /** The in-flight page request, cancelled whenever the query changes. */
    private var loadJob: Job? = null

    /** `skip` for the next page; advanced only by successful loads. */
    private var nextSkip = 0

    /**
     * Guards against overlapping page requests.
     *
     * Set synchronously, before `launch`, because the coroutine body — and so
     * `isLoadingMore` in the UI state — only runs on a later dispatch. Two
     * scroll callbacks in the same frame would otherwise both pass the check
     * and append the same page twice, producing duplicate ids.
     */
    private var isPaging = false

    /**
     * The query the products currently on screen were loaded with.
     *
     * Distinct from `uiState.query`, which changes on every keystroke: paging
     * must keep using the query that produced the current page, or a scroll
     * during the debounce window would append results for a different search.
     */
    private var activeQuery = ""

    /**
     * The domain products backing [uiState], keyed by id.
     *
     * [ProductListUiState.products] never carries the domain model (see
     * [ProductUiState]), so this is what [onToggleFavorite] resolves an id
     * back into a [Product] against, and what every UI state update below
     * re-projects into rows — a single write path rather than a UI list
     * patched in place, so it can never drift from the ids it was built from.
     */
    private var loadedProducts: List<Product> = emptyList()

    private var favoriteIds: Set<Int> = emptySet()

    init {
        getFavoriteProducts()
            .map { favorites -> favorites.map(Product::id).toSet() }
            .onEach { ids ->
                favoriteIds = ids
                _uiState.update { it.copy(products = loadedProducts.toUiStates()) }
            }
            .launchIn(scope)

        queryInput
            .debounce { query -> if (query.isEmpty()) 0L else SEARCH_DEBOUNCE_MILLIS }
            .distinctUntilChanged()
            .onEach { query -> loadFirstPage(query) }
            .launchIn(scope)
    }

    /** Called on every keystroke; the debounce above throttles the actual search. */
    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
        queryInput.value = query
    }

    fun onClearQuery() = onQueryChange("")

    fun onRetry() = loadFirstPage(activeQuery)

    /**
     * Requests the next page.
     *
     * Ignored while a page request is in flight or the catalog is exhausted, so
     * scroll callbacks can fire freely without duplicating pages.
     */
    fun onLoadMore() {
        val state = _uiState.value
        if (isPaging || state.isLoading || !state.canLoadMore) return
        isPaging = true

        loadJob = scope.launch {
            try {
                _uiState.update { it.copy(isLoadingMore = true, error = null) }
                when (val result = getProducts(query = activeQuery, skip = nextSkip)) {
                    is DataResult.Success -> {
                        val page = result.data
                        nextSkip = page.nextSkip
                        loadedProducts = loadedProducts + page.products
                        _uiState.update {
                            it.copy(
                                products = loadedProducts.toUiStates(),
                                canLoadMore = page.hasMore,
                                isLoadingMore = false,
                            )
                        }
                    }

                    is DataResult.Failure -> _uiState.update {
                        it.copy(isLoadingMore = false, error = result.error.toErrorMessage())
                    }
                }
            } finally {
                // finally, not a trailing statement: a cancelled page request
                // would otherwise strand the flag and block paging forever.
                isPaging = false
            }
        }
    }

    fun onToggleFavorite(id: Int) {
        val product = loadedProducts.firstOrNull { it.id == id } ?: return
        scope.launch { toggleFavorite(product) }
    }

    /**
     * Loads page one for [query], replacing whatever is on screen.
     *
     * Cancels any in-flight request first so a slow response for an old query
     * cannot overwrite results for a newer one.
     */
    private fun loadFirstPage(query: String) {
        loadJob?.cancel()
        isPaging = false
        nextSkip = 0
        activeQuery = query

        loadJob = scope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    isLoadingMore = false,
                    error = null,
                )
            }
            when (val result = getProducts(query = query, skip = 0)) {
                is DataResult.Success -> {
                    val page = result.data
                    nextSkip = page.nextSkip
                    loadedProducts = page.products
                    _uiState.update {
                        it.copy(
                            products = loadedProducts.toUiStates(),
                            canLoadMore = page.hasMore,
                            isLoading = false,
                        )
                    }
                }

                is DataResult.Failure -> {
                    loadedProducts = emptyList()
                    _uiState.update {
                        it.copy(
                            products = emptyList(),
                            canLoadMore = false,
                            isLoading = false,
                            error = result.error.toErrorMessage(),
                        )
                    }
                }
            }
        }
    }

    private fun List<Product>.toUiStates(): List<ProductUiState> = map { product ->
        ProductUiState(
            id = product.id,
            title = product.title,
            category = product.category,
            thumbnail = product.thumbnail,
            formattedPrice = productFormatter.formatPrice(product.price),
            formattedRating = productFormatter.formatRating(product.rating),
            stock = product.stock,
            hasDiscount = product.discountPercentage > 0,
            formattedDiscount = productFormatter.formatDiscount(product.discountPercentage),
            isFavorite = product.id in favoriteIds,
        )
    }

    private companion object {
        const val SEARCH_DEBOUNCE_MILLIS = 500L
    }
}
