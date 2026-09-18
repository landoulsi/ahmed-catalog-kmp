package com.landoulsi.catalog.shared.presentation.favorites

import com.landoulsi.catalog.shared.core.DispatcherProvider
import com.landoulsi.catalog.shared.domain.model.Product
import com.landoulsi.catalog.shared.domain.usecase.GetFavoriteProductsUseCase
import com.landoulsi.catalog.shared.domain.usecase.ToggleFavoriteProductUseCase
import com.landoulsi.catalog.shared.presentation.common.BaseViewModel
import com.landoulsi.catalog.shared.presentation.common.ProductFormatter
import com.landoulsi.catalog.shared.presentation.common.ProductUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * Reads straight from the favorites flow, so removing a favorite here needs no
 * manual refresh — the repository emits and the list updates.
 */
class FavoriteProductsViewModel(
    private val toggleFavorite: ToggleFavoriteProductUseCase,
    getFavoriteProducts: GetFavoriteProductsUseCase,
    dispatcherProvider: DispatcherProvider,
    private val productFormatter: ProductFormatter,
) : BaseViewModel<FavoriteProductsUiState>(dispatcherProvider) {

    private val _uiState = MutableStateFlow(FavoriteProductsUiState())
    override val uiState: StateFlow<FavoriteProductsUiState> = _uiState.asStateFlow()

    /**
     * The domain favorites backing [uiState], keyed by id.
     *
     * [FavoriteProductsUiState.favorites] never carries the domain model (see
     * [ProductUiState]), so this is what [onRemoveFavorite] resolves an id
     * back into a [Product] against.
     */
    private var loadedFavorites: List<Product> = emptyList()

    init {
        getFavoriteProducts()
            .onEach { favorites ->
                loadedFavorites = favorites
                val uiModels = favorites.map { product ->
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
                        isFavorite = true,
                    )
                }
                _uiState.value = FavoriteProductsUiState(favorites = uiModels, isLoading = false)
            }
            .launchIn(scope)
    }

    fun onRemoveFavorite(id: Int) {
        val product = loadedFavorites.firstOrNull { it.id == id } ?: return
        scope.launch { toggleFavorite(product) }
    }
}
