package com.landoulsi.catalog.shared.presentation.details

import com.landoulsi.catalog.shared.core.DataResult
import com.landoulsi.catalog.shared.core.DispatcherProvider
import com.landoulsi.catalog.shared.domain.model.Product
import com.landoulsi.catalog.shared.domain.usecase.GetProductDetailsUseCase
import com.landoulsi.catalog.shared.domain.usecase.IsFavoriteProductUseCase
import com.landoulsi.catalog.shared.domain.usecase.ToggleFavoriteProductUseCase
import com.landoulsi.catalog.shared.presentation.common.BaseViewModel
import com.landoulsi.catalog.shared.presentation.common.ProductFormatter
import com.landoulsi.catalog.shared.presentation.common.toErrorMessage
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Drives the product details screen.
 *
 * The product id is a constructor parameter rather than something passed to a
 * `load()` method: the ViewModel is meaningless without it, so making it
 * required removes an entire class of uninitialized-state bugs.
 */
class ProductDetailsViewModel(
    private val productId: Int,
    private val getProductDetails: GetProductDetailsUseCase,
    private val toggleFavorite: ToggleFavoriteProductUseCase,
    isFavorite: IsFavoriteProductUseCase,
    dispatcherProvider: DispatcherProvider,
    private val productFormatter: ProductFormatter,
) : BaseViewModel<ProductDetailsUiState>(dispatcherProvider) {

    private val _uiState = MutableStateFlow(ProductDetailsUiState())
    override val uiState: StateFlow<ProductDetailsUiState> = _uiState.asStateFlow()

    /**
     * The loaded domain product backing [uiState], so [onToggleFavorite] has
     * something to pass to the repository. [ProductDetailsUiState] never
     * carries the domain model itself (see [ProductDetailsContent]).
     */
    private var loadedProduct: Product? = null
    private var loadJob: Job? = null

    init {
        isFavorite(productId)
            .onEach { isFavorite -> _uiState.update { it.copy(isFavorite = isFavorite) } }
            .launchIn(scope)

        load()
    }

    fun onRetry() = load()

    fun onToggleFavorite() {
        val product = loadedProduct ?: return
        scope.launch { toggleFavorite(product) }
    }

    private fun load() {
        loadJob?.cancel()
        loadJob = scope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = getProductDetails(productId)) {
                is DataResult.Success -> {
                    val product = result.data
                    loadedProduct = product
                    _uiState.update {
                        it.copy(
                            content = ProductDetailsContent(
                                title = product.title,
                                brand = product.brand,
                                category = product.category,
                                description = product.description,
                                imageUrl = product.images.firstOrNull() ?: product.thumbnail,
                                formattedPrice = productFormatter.formatPrice(product.discountedPrice),
                                hasDiscount = product.discountPercentage > 0,
                                formattedDiscount = productFormatter.formatDiscount(product.discountPercentage),
                                formattedRating = productFormatter.formatRating(product.rating),
                                isInStock = product.isInStock,
                                stock = product.stock,
                            ),
                            isLoading = false,
                        )
                    }
                }

                is DataResult.Failure -> _uiState.update {
                    it.copy(isLoading = false, error = result.error.toErrorMessage())
                }
            }
        }
    }
}
