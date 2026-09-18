# CODEMAP — product-catalog-kmp

_Grep this to locate code — do NOT read it whole (101 files, 322 symbols):_
_  `grep -n 'SymbolName' CODEMAP.md`  ·  `grep -nA20 '^### dir/path' CODEMAP.md`_
_The '## Main flows' block below is short — read that directly for call paths._
_Generated 2026-09-20T21:35:04+04:00 by `gen_codemap.sh`. Regenerate: `gen_codemap.sh /Users/ahmed/Work/product-catalog-kmp`_

<!-- HAND-WRITTEN: edit freely — regeneration keeps this block -->
## Main flows (fill this in — a few lines, updated rarely)
<!-- e.g. Nearby search: SearchOrchestrator.kt -> PlacesService.kt -> Repository.kt -->
<!-- END HAND-WRITTEN -->

<!-- AUTO-GENERATED BELOW — do not edit; run gen_codemap.sh -->

### androidApp/src/androidTest/kotlin/com/landoulsi/catalog/ui

**TestData.kt**
  - fun testProduct()  (8)
  - fun testProductUiState()  (28)

### androidApp/src/androidTest/kotlin/com/landoulsi/catalog/ui/details

**ProductDetailsScreenTest.kt**
  - class ProductDetailsScreenTest  (20)
  - fun testContent()  (27)
  - fun setContent()  (44)
  - fun showsTheLoadedProductsTitleAndDescription()  (63)
  - fun favoriteIconIsHiddenUntilTheProductHasLoaded()  (76)
  - fun tappingFavoriteIconInvokesCallback()  (84)
  - fun inFavoriteProductShowsRemoveDescription()  (102)
  - fun errorStateShowsRetryButtonThatInvokesCallback()  (116)
  - fun tappingBackInvokesCallback()  (134)

### androidApp/src/androidTest/kotlin/com/landoulsi/catalog/ui/favorites

**FavoriteProductsScreenTest.kt**
  - class FavoriteProductsScreenTest  (19)
  - fun setContent()  (26)
  - fun showsEmptyStateWhenNothingIsSaved()  (43)
  - fun showsEachSavedProduct()  (52)
  - fun everyRowShowsTheRemoveDescriptionSinceAllAreFavorites()  (68)
  - fun tappingRemoveInvokesCallbackWithThatProductId()  (84)
  - fun tappingAFavoriteRowReportsItsId()  (99)

### androidApp/src/androidTest/kotlin/com/landoulsi/catalog/ui/products

**ProductsScreenTest.kt**
  - class ProductsScreenTest  (33)
  - fun setContent()  (41)
  - fun showsEachProductTitleInTheList()  (66)
  - fun showsEmptyStateWhenNoProductsAndNoQuery()  (81)
  - fun showsNoResultsStateWhenQueryProducesNothing()  (90)
  - fun showsSearchBar()  (99)
  - fun typingInSearchBarReportsQuery()  (108)
  - fun tappingClearButtonClearsTheQuery()  (123)
  - fun tappingAProductRowReportsItsId()  (137)
  - fun tappingTheFavoriteIconReportsTheProduct()  (150)
  - fun inFavoriteProductShowsRemoveDescriptionInsteadOfAdd()  (165)
  - fun showsErrorStateWhenLoadingFails()  (176)
  - fun tappingRetryInErrorStateReportsRetry()  (187)
  - fun scrollingNearTheBottomTriggersLoadMore()  (211)

### androidApp/src/debug/kotlin/com/landoulsi/catalog/ui

**PreviewData.kt**
  - fun previewProduct()  (7)
  - fun previewProductUiState()  (26)

### androidApp/src/debug/kotlin/com/landoulsi/catalog/ui/common

**ProductRowPreviews.kt**
  - fun ProductRowNotInFavoritePreview()  (23)
  - fun ProductRowInFavoritePreview()  (35)
  - fun ProductRowLongTitlePreview()  (47)

**StateViewsPreviews.kt**
  - fun LoadingStatePreview()  (9)
  - fun ErrorStatePreview()  (18)
  - fun EmptyStatePreview()  (26)
  - fun EmptyStateWithSubtitlePreview()  (34)

### androidApp/src/debug/kotlin/com/landoulsi/catalog/ui/details

**ProductDetailsScreenPreviews.kt**
  - fun ProductDetailsScreenLoadedPreview()  (26)
  - fun ProductDetailsScreenLoadingPreview()  (43)
  - fun ProductDetailsScreenErrorPreview()  (56)
  - fun ProductDetailsTopBarNotInFavoritePreview()  (73)
  - fun ProductDetailsTopBarInFavoritePreview()  (87)
  - fun ProductDetailsTopBarLoadingPreview()  (101)

### androidApp/src/debug/kotlin/com/landoulsi/catalog/ui/favorites

**FavoriteProductsScreenPreviews.kt**
  - fun FavoriteProductsScreenEmptyPreview()  (11)
  - fun FavoriteProductsScreenListPreview()  (23)
  - fun FavoriteProductsTopBarPreview()  (41)

### androidApp/src/debug/kotlin/com/landoulsi/catalog/ui/products

**ProductsScreenPreviews.kt**
  - fun ProductsScreenLoadingPreview()  (12)
  - fun ProductsScreenListPreview()  (28)
  - fun ProductsScreenEmptyPreview()  (50)
  - fun ProductsScreenErrorPreview()  (66)
  - fun ProductsTopBarPreview()  (82)
  - fun ProductsSearchFieldEmptyPreview()  (90)
  - fun ProductsSearchFieldWithQueryPreview()  (98)

### androidApp/src/main/kotlin/com/landoulsi/catalog

**CatalogApplication.kt**
  - class CatalogApplication  (11)
  - fun onCreate()  (13)

**MainActivity.kt**
  - class MainActivity  (16)
  - fun onCreate()  (18)

### androidApp/src/main/kotlin/com/landoulsi/catalog/navigation

**CatalogDestination.kt**
  - interface CatalogDestination  (12)
  - object Products  (15)
  - class ProductDetails  (18)
  - object Favorites  (21)

**CatalogNavHost.kt**
  - fun CatalogNavHost()  (36)
  - fun CatalogBottomBar()  (107)

**CatalogTab.kt**
  - class CatalogTab  (4)

### androidApp/src/main/kotlin/com/landoulsi/catalog/ui/common

**ErrorMessages.kt**
  - fun ErrorMessage.toUiText()  (15)

**ProductRow.kt**
  - fun ProductRow()  (39)

**StateViews.kt**
  - fun LoadingState()  (23)
  - fun ErrorState()  (33)
  - fun EmptyState()  (61)

**UiText.kt**
  - interface UiText  (14)
  - class Res  (15)
  - class Plain  (16)
  - fun UiText.resolve()  (20)

### androidApp/src/main/kotlin/com/landoulsi/catalog/ui/details

**ProductDetailsScreen.kt**
  - fun ProductDetailsRoute()  (49)
  - fun ProductDetailsScreen()  (74)
  - fun ProductDetailsTopBar()  (253)

### androidApp/src/main/kotlin/com/landoulsi/catalog/ui/favorites

**FavoriteProductsScreen.kt**
  - fun FavoriteProductsRoute()  (30)
  - fun FavoriteProductsScreen()  (47)
  - fun FavoriteProductsTopBar()  (93)

### androidApp/src/main/kotlin/com/landoulsi/catalog/ui/products

**ProductsScreen.kt**
  - fun ProductsRoute()  (71)
  - fun ProductsScreen()  (100)
  - fun ProductsTopBar()  (232)
  - fun ProductsSearchField()  (254)

### androidApp/src/main/kotlin/com/landoulsi/catalog/ui/theme

**Theme.kt**
  - fun ProductCatalogTheme()  (53)

### androidApp/src/test/kotlin/com/landoulsi/catalog/ui/common

**ErrorMessagesTest.kt**
  - class ErrorMessagesTest  (8)
  -   (11)
  -   (18)
  -   (25)

### shared/src/androidHostTest/kotlin/com/landoulsi/catalog/shared/presentation/common

**ProductFormatterImplTest.kt**
  - class ProductFormatterImplTest  (7)
  -   (10)
  -   (19)
  -   (27)
  -   (37)
  -   (46)
  -   (53)
  -   (64)

### shared/src/androidMain/kotlin/com/landoulsi/catalog/shared/core

**Logger.android.kt**
  - fun error()  (8)
  - fun debug()  (12)

### shared/src/androidMain/kotlin/com/landoulsi/catalog/shared/presentation/common

**ProductFormatterImpl.kt**
  - class ProductFormatterImpl  (11)
  - fun formatPrice()  (19)
  - fun formatRating()  (22)
  - fun formatDiscount()  (25)

### shared/src/commonMain/kotlin/com/landoulsi/catalog/shared/core

**DataResult.kt**
  - interface DataResult<out  (9)
  - class Success<out  (10)
  - class Failure  (11)
  - interface CatalogError  (15)
  - object NoConnection  (17)
  - object Timeout  (20)
  - class Server  (23)
  - object Serialization  (26)
  - class Unknown  (29)
  -   (32)

**DispatcherProvider.kt**
  - interface DispatcherProvider  (6)
  - class DispatcherProviderImpl  (14)

**Logger.kt**
  - interface Logger  (3)
  - fun error()  (4)
  - fun debug()  (5)
  - class LoggerImpl  (11)

### shared/src/commonMain/kotlin/com/landoulsi/catalog/shared/data/local

**FavoriteProductsStorage.kt**
  - interface FavoriteProductsStorage  (5)
  - fun read()  (7)
  - fun write()  (10)

**SqlDelightFavoriteProductsStorage.kt**
  - class SqlDelightFavoriteProductsStorage  (10)
  - fun read()  (16)
  - fun write()  (24)
  - fun FavoriteProduct.toProduct()  (47)

### shared/src/commonMain/kotlin/com/landoulsi/catalog/shared/data/mapper

**ProductMapper.kt**
  - interface ProductMapper  (9)
  - fun toDomain()  (10)
  - fun toDomain()  (11)
  - class ProductMapperImpl  (14)
  - fun toDomain()  (16)
  - fun toDomain()  (32)

### shared/src/commonMain/kotlin/com/landoulsi/catalog/shared/data/remote

**CatalogApi.kt**
  - class CatalogApi  (14)
  - fun getProducts()  (18)
  - fun searchProducts()  (24)
  - fun getProduct()  (31)

**ErrorMapper.kt**
  - fun Throwable.toCatalogError()  (14)

**HttpClientFactory.kt**
  - object HttpClientFactory  (18)
  - fun create()  (28)
  - fun log()  (51)

### shared/src/commonMain/kotlin/com/landoulsi/catalog/shared/data/remote/dto

**ProductDto.kt**
  - class ProductDto  (8)

**ProductListResponseDto.kt**
  - class ProductListResponseDto  (8)

### shared/src/commonMain/kotlin/com/landoulsi/catalog/shared/data/repository

**FavoriteProductsRepositoryImpl.kt**
  - class FavoriteProductsRepositoryImpl  (30)
  - fun isFavorite()  (42)
  - fun toggleFavorite()  (46)

**ProductRepositoryImpl.kt**
  - class ProductRepositoryImpl  (16)
  - fun getProducts()  (22)
  - fun getProduct()  (36)
  -   (46)

### shared/src/commonMain/kotlin/com/landoulsi/catalog/shared/di

**HttpLogLevel.kt**
  - class HttpLogLevel  (6)

**PlatformHttpEngine.kt**
  - class PlatformHttpEngine  (9)

### shared/src/commonMain/kotlin/com/landoulsi/catalog/shared/domain/model

**Product.kt**
  - class Product  (5)

**ProductPage.kt**
  - class ProductPage  (6)

### shared/src/commonMain/kotlin/com/landoulsi/catalog/shared/domain/repository

**FavoriteProductsRepository.kt**
  - interface FavoriteProductsRepository  (10)
  - fun isFavorite()  (16)
  - fun toggleFavorite()  (18)

**ProductRepository.kt**
  - interface ProductRepository  (7)
  - fun getProducts()  (13)
  - fun getProduct()  (15)

### shared/src/commonMain/kotlin/com/landoulsi/catalog/shared/domain/usecase

**GetFavoriteProductsUseCase.kt**
  - class GetFavoriteProductsUseCase  (7)
  - fun invoke()  (10)

**GetProductDetailsUseCase.kt**
  - class GetProductDetailsUseCase  (7)
  - fun invoke()  (10)

**GetProductsUseCase.kt**
  - class GetProductsUseCase  (11)
  - fun invoke()  (14)

**IsFavoriteProductUseCase.kt**
  - class IsFavoriteProductUseCase  (6)
  - fun invoke()  (9)

**ToggleFavoriteProductUseCase.kt**
  - class ToggleFavoriteProductUseCase  (6)
  - fun invoke()  (9)

### shared/src/commonMain/kotlin/com/landoulsi/catalog/shared/presentation/common

**BaseViewModel.kt**
  - class BaseViewModel<S  (21)
  - fun onCleared()  (36)

**CurrencyProvider.kt**
  - class CurrencyProvider  (13)

**ErrorMessage.kt**
  - class ErrorMessage  (13)
  - fun CatalogError.toErrorMessage()  (20)

**ProductFormatter.kt**
  - interface ProductFormatter  (6)
  - fun formatPrice()  (9)
  - fun formatRating()  (12)
  - fun formatDiscount()  (15)

**ProductUiState.kt**
  - class ProductUiState  (11)

**UiState.kt**
  - interface UiState  (6)

### shared/src/commonMain/kotlin/com/landoulsi/catalog/shared/presentation/details

**ProductDetailsUiState.kt**
  - class ProductDetailsContent  (11)
  - class ProductDetailsUiState  (25)

**ProductDetailsViewModel.kt**
  - class ProductDetailsViewModel  (28)
  - fun onRetry()  (56)
  - fun onToggleFavorite()  (58)
  - fun load()  (63)

### shared/src/commonMain/kotlin/com/landoulsi/catalog/shared/presentation/favorites

**FavoriteProductsUiState.kt**
  - class FavoriteProductsUiState  (6)

**FavoriteProductsViewModel.kt**
  - class FavoriteProductsViewModel  (21)
  - fun onRemoveFavorite()  (63)

### shared/src/commonMain/kotlin/com/landoulsi/catalog/shared/presentation/products

**ProductsUiState.kt**
  - class ProductsUiState  (13)

**ProductsViewModel.kt**
  - class ProductsViewModel  (34)
  - fun onQueryChange()  (103)
  - fun onClearQuery()  (108)
  - fun onRetry()  (110)
  - fun onLoadMore()  (118)
  - fun onToggleFavorite()  (152)
  - fun loadFirstPage()  (163)
  -   (206)

### shared/src/commonTest/kotlin/com/landoulsi/catalog/shared/data

**FavoriteProductsRepositoryImplTest.kt**
  - class FavoriteProductsRepositoryImplTest  (14)
  -   (17)
  -   (28)
  -   (39)
  -   (52)
  -   (62)
  -   (78)

**ProductMapperTest.kt**
  - class ProductMapperTest  (11)
  -   (16)
  -   (46)
  -   (55)
  -   (67)

**ProductRepositoryImplTest.kt**
  - class ProductRepositoryImplTest  (26)
  - fun repository()  (33)
  - fun MockRequestHandleScope.json()  (46)
  -   (52)
  -   (65)
  -   (77)
  -   (89)
  -   (99)

**SqlDelightFavoriteProductsStorageTest.kt**
  - class SqlDelightFavoriteProductsStorageTest  (11)
  - fun setUp()  (16)
  -   (22)
  -   (27)

### shared/src/commonTest/kotlin/com/landoulsi/catalog/shared/data/remote

**ApiResponseParsingTest.kt**
  - class ApiResponseParsingTest  (17)
  -   (26)
  -   (57)
  -   (66)
  -   (89)
  -   (120)
  -   (150)
  -   (160)

### shared/src/commonTest/kotlin/com/landoulsi/catalog/shared/domain

**ProductPageTest.kt**
  - class ProductPageTest  (10)
  -   (13)
  -   (26)
  -   (40)

**ProductTest.kt**
  - class ProductTest  (9)
  -   (12)
  -   (20)
  -   (26)
  -   (33)

### shared/src/commonTest/kotlin/com/landoulsi/catalog/shared/domain/usecase

**GetFavoriteProductsUseCaseTest.kt**
  - class GetFavoriteProductsUseCaseTest  (8)
  -   (14)

**GetProductDetailsUseCaseTest.kt**
  - class GetProductDetailsUseCaseTest  (12)
  -   (15)
  -   (26)

**GetProductsUseCaseTest.kt**
  - class GetProductsUseCaseTest  (14)
  -   (20)
  -   (30)
  -   (40)
  -   (56)

**IsFavoriteProductUseCaseTest.kt**
  - class IsFavoriteProductUseCaseTest  (8)
  -   (14)

**ToggleFavoriteProductUseCaseTest.kt**
  - class ToggleFavoriteProductUseCaseTest  (9)
  -   (15)

### shared/src/commonTest/kotlin/com/landoulsi/catalog/shared/fake

**FakeFavoriteProductsRepository.kt**
  - class FakeFavoriteProductsRepository  (10)
  - fun isFavorite()  (18)
  - fun toggleFavorite()  (21)

**FakeFavoriteProductsStorage.kt**
  - class FakeFavoriteProductsStorage  (10)
  - fun read()  (20)
  - fun write()  (22)

**FakeLogger.kt**
  - class FakeLogger  (5)
  - fun error()  (6)
  - fun debug()  (7)

**FakeProductFormatter.kt**
  - class FakeProductFormatter  (10)
  - fun formatPrice()  (28)
  - fun formatRating()  (30)
  - fun formatDiscount()  (32)

**FakeProductRepository.kt**
  - class FakeProductRepository  (10)
  - class Call  (16)
  - fun getProducts()  (24)
  - fun getProduct()  (38)
  - fun testProduct()  (42)

**RecordingRepositories.kt**
  - class ProductCall  (11)
  - class RecordingProductRepository  (14)
  - fun getProducts()  (24)
  - fun getProduct()  (33)
  - class RecordingFavoriteProductsRepository  (39)
  - fun isFavorite()  (52)
  - fun toggleFavorite()  (57)

**TestDispatcherProvider.kt**
  - class TestDispatcherProvider  (17)

**TestDtoData.kt**
  - fun testProductDto()  (7)
  - fun testProductListResponseDto()  (28)

### shared/src/commonTest/kotlin/com/landoulsi/catalog/shared/presentation

**FavoriteProductsViewModelTest.kt**
  - class FavoriteProductsViewModelTest  (22)
  - fun TestScope.viewModel()  (26)
  -   (34)
  -   (47)
  -   (57)
  -   (65)
  -   (78)

**ProductDetailsViewModelTest.kt**
  - class ProductDetailsViewModelTest  (27)
  - fun TestScope.viewModel()  (32)
  -   (43)
  -   (62)
  -   (74)
  -   (92)

**ProductsViewModelTest.kt**
  - class ProductsViewModelTest  (27)
  - fun TestScope.viewModel()  (32)
  -   (41)
  -   (59)
  -   (72)
  -   (88)
  -   (105)
  -   (125)
  -   (144)
  -   (168)
  -   (185)
  -   (203)
  -   (228)
  -   (240)

### shared/src/iosMain/kotlin/com/landoulsi/catalog/shared/core

**Logger.ios.kt**
  - fun error()  (4)
  - fun debug()  (8)

### shared/src/iosMain/kotlin/com/landoulsi/catalog/shared/di

**IosPlatformModule.kt**
  - class IosKoinHelper  (34)
  - fun productsViewModel()  (35)
  - fun favoritesViewModel()  (36)
  - fun productDetailsViewModel()  (37)
  - fun initKoinIos()  (46)

### shared/src/iosMain/kotlin/com/landoulsi/catalog/shared/presentation/common

**ProductFormatterImpl.kt**
  - class ProductFormatterImpl  (9)
  - fun formatPrice()  (13)
  - fun formatRating()  (16)
  - fun formatDiscount()  (19)
