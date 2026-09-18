# Product Catalog — Kotlin Multiplatform

A product catalog built with Kotlin Multiplatform and Clean Architecture. All
business logic — networking, persistence, use cases and ViewModels — lives in a
shared module; the Android app is a thin Compose layer over it.

Data comes from the public [DummyJSON](https://dummyjson.com) products API.

## Deliverables

| Deliverable | Where |
| --- | --- |
| Git repository | This repo — granted to `muhammed.peedikayil@Qmobility.ae` |
| README | Setup below, [Architecture](#architecture), [iOS considerations](#multiplatform-kmp-design--ios-readiness) |
| Working Android app, all three screens | [Screens](#screens), [Setup](#setup) |
| Unit tests (6 minimum) | 61 tests in `:shared` — see [Testing](#testing) |

## Screens

| Screen | What it does |
| --- | --- |
| **Products** | Paginated catalog, search by name (debounced), favorite toggle inline |
| **Product details** | Full product record, favorite/unfavorite |
| **Favorites** | Locally persisted favorites, surviving app restarts |
| **Theme** | Light / Dark, following the device setting |

A bottom navigation bar (List / Favorites) switches between the two top-level
tabs; it stays visible on the details screen too. Tapping a product pushes
Product Details on top of whichever tab it was opened from.

## Setup

Requirements: a JDK (17 or newer) and the Android SDK with API 37. The build
targets JDK 17 via Gradle toolchains and provisions it automatically if it is
not installed, so no specific JDK has to be set up by hand.

```bash
git clone <repo-url>
cd product-catalog-kmp

# Point the build at your SDK (or let Android Studio create it)
echo "sdk.dir=$HOME/Library/Android/sdk" > local.properties

./gradlew :androidApp:installDebug               # build + install on a running device
./gradlew :shared:testAndroidHostTest            # run shared module unit tests (61 tests)
./gradlew :androidApp:testDebugUnitTest          # run Android unit tests
./gradlew :androidApp:connectedDebugAndroidTest  # run Compose UI instrumented tests (device/emulator)
```

Useful checks:

```bash
./gradlew :shared:compileKotlinIosArm64   # proves commonMain stays platform-agnostic
./gradlew :androidApp:assembleDebug       # assemble Android debug APK
./gradlew build                           # build all targets
```

## Architecture

Three layers inside `:shared`, with dependencies pointing strictly inward.

```
:androidApp
  Compose screens, Navigation, Android resources
  Route composables (stateful) -> Screen composables (stateless)
      |
      | observes StateFlow<UiState>
      v
:shared / presentation
  ProductsViewModel, ProductDetailsViewModel, FavoritesViewModel
  UiState data classes, ErrorMessage (platform-neutral)
      |
      | calls
      v
:shared / domain   <- knows nothing about Ktor, Android, iOS
  Product, ProductPage
  ProductRepository, FavoritesRepository (interfaces)
  GetProducts, GetProductDetails, ToggleFavorite,
  GetFavorites, IsFavorite (use cases)
      ^
      | implements
      |
:shared / data
  CatalogApi (Ktor), DTOs, mappers, repository impls
  FavoriteProductsStorage -> SQLDelight
```

### Layer rules

**Domain** is pure Kotlin: models, repository *interfaces*, and use cases. It has
no dependency on Ktor, Android, or any serialization library, so it can be
reasoned about and tested in isolation.

**Data** implements the domain's interfaces. DTOs are separate from domain models
and every API field that may be absent is nullable there; a `ProductMapper`
interface, injected into the repositories, turns them into valid domain
objects — so the data layer depends on an abstraction rather than a top-level
function, and a test double can stand in for it if a repository test ever needs
one. Transport exceptions are translated into a `CatalogError` sealed interface
at this boundary, so nothing above the data layer ever sees a Ktor type.

**Presentation** holds the ViewModels. Each exposes a single
`StateFlow<SomeUiState>` and accepts plain function calls — no Compose types, no
`LiveData`, no Android imports. One immutable state object per screen means the
UI can never render a half-applied update.

### Key decisions

**`DataResult` instead of exceptions.** Operations that can fail return
`DataResult.Success | DataResult.Failure(CatalogError)`. Error handling is
explicit at each boundary and the compiler enforces that callers consider it.
`CancellationException` is deliberately rethrown so structured concurrency keeps
working.

**Favorites as a single source of truth.** `FavoritesRepositoryImpl` reads
storage once into a `StateFlow` and every screen observes it, so favoriting a
product on the details screen updates the list and favorites screens
immediately, with no manual refresh. Writes are serialized with a `Mutex`.

**`FavoritesStorage` speaks the domain model, not a transport DTO.**
`read()`/`write()` work in `Product` directly rather than the remote
`ProductDto`, so the local persistence layer has no dependency on the shape of
DummyJSON's API.

**UI state never carries the domain model.** `ProductUiState` and
`ProductDetailsContent` expose only the fields a row/screen renders — a
formatted price string, not the raw `Double`; a `hasDiscount` flag, not the
discount percentage — so Compose (or SwiftUI) code can't bypass the
presentation layer's formatting by reaching into `Product` directly. Each
ViewModel keeps the domain products it loaded privately, so a favorite toggle
by id can still resolve the full record the repository needs to persist.

**Search and listing share one code path.** `/products` and `/products/search`
return the same envelope, so `ProductRepository.getProducts(query, skip, limit)`
covers both, and paging rules are written once. Pagination uses the response's
`total` to decide whether more pages exist.

**Paging tracks the active query.** `ProductsViewModel` keeps `activeQuery`
separate from the live text field value. Without it, a scroll landing inside the
300 ms search debounce would append results for a different query onto the
visible list.

**Dispatchers are injected.** ViewModels take a `DispatcherProvider` and run work
on a scope built from its `main` dispatcher rather than `viewModelScope`.
`FavoritesRepositoryImpl` uses the same provider's `io` dispatcher to move
`SqlDelightFavoriteProductsStorage`'s blocking SQLite writes off of it — the only
blocking call in the data layer, since Ktor's suspending HTTP calls don't need
offloading. `io` is backed by `expect fun ioDispatcher()`: `Dispatchers.IO` on
Android (an elastic pool sized for many concurrent blocking calls), and
`Dispatchers.Default` on iOS, where `Dispatchers.IO` doesn't exist. Tests supply
`TestDispatcherProvider` (unconfined, sharing the test
scheduler), which removes `Dispatchers.setMain`/`resetMain` scaffolding
entirely.

**Error text is named, not written, in shared code.** ViewModels emit an
`ErrorMessage` enum; each platform maps it to its own localized string. The
Android mapping is an exhaustive `when` with no `else`, so adding a variant to
shared code becomes a compile error rather than a silent generic message.

**Strings and dimensions are resources.** No user-facing string or `dp` literal
appears in Compose code — everything resolves through `strings.xml` (translatable)
and `dimens.xml` (4dp spacing grid).

## Dependency injection — Koin

Dependency injection is powered by [Koin](https://insert-koin.io/) (Koin 4.x), matching the requirements of the project. DI configuration is divided into modular, scoped definitions:

- **`sharedModule`:** Provides common singletons such as JSON configuration (`Json`), the shared `HttpClient` (via `HttpClientFactory`), and `FavoritesDatabase`.
- **`androidPlatformModule`:** Android-specific platform bindings providing `PlatformHttpEngine` (OkHttp) and `SqlDriver` (`AndroidSqliteDriver`).
- **`iosPlatformModule`:** iOS-specific platform bindings providing `PlatformHttpEngine` (Darwin) and `SqlDriver` (`NativeSqliteDriver`), along with `initKoinIos()` for Swift app startup.
- **`dataModule`:** Binds data-layer implementations to domain interfaces: `DispatcherProviderImpl` -> `DispatcherProvider`, `CatalogApi`, `SqlDelightFavoriteProductsStorage` -> `FavoriteProductsStorage`, `ProductRepositoryImpl` -> `ProductRepository`, and `FavoriteProductsRepositoryImpl` -> `FavoriteProductsRepository`.
- **`domainModule`:** Declares use-case factories (`GetProductsUseCase`, `GetProductDetailsUseCase`, `GetFavoritesUseCase`, `IsFavoriteUseCase`, `ToggleFavoriteUseCase`).
- **`presentationModule`:** Declares ViewModel definitions for `ProductsViewModel`, `FavoritesViewModel`, and `ProductDetailsViewModel` (accepting runtime `productId`).

Compose screens obtain ViewModels directly via Koin's Compose Multiplatform integration (`koinViewModel()`):

```kotlin
val viewModel: ProductsViewModel = koinViewModel()
```

For `ProductDetailsViewModel`, which requires a runtime `productId` from navigation, parameters are passed dynamically:

```kotlin
val viewModel: ProductDetailsViewModel = koinViewModel(
    key = "product-$productId",
    parameters = { parametersOf(productId) },
)
```

On Android, Koin is bootstrapped in `CatalogApplication.onCreate()` via standard `startKoin { androidContext(...); modules(...) }`.

## Testing

The project incorporates comprehensive testing at multiple layers:

### 1. Shared module unit tests (61 tests)

```bash
./gradlew :shared:testAndroidHostTest
```

**61 tests** in `commonTest`, covering DTO parsing, mappers, domain models,
repositories, and ViewModels:

| Area | Tests | What they cover |
| --- | --- | --- |
| `ApiResponseParsingTest` | 6 | Raw DummyJSON JSON decoded through the app's real `Json` config: full product, missing optional fields, unmodeled extra fields, list envelope, empty envelope, envelope missing paging fields |
| `ProductMapperTest` | 4 | Full mapping, missing-field defaults, thumbnail fallback, list envelope |
| `ProductTest` | 4 | Discount arithmetic, nearest-cent rounding, stock availability |
| `ProductPageTest` | 3 | `hasMore` / `nextSkip` at page boundaries |
| `ProductRepositoryImplTest` | 5 | Endpoint routing (list vs. search), domain mapping, HTTP error → `CatalogError.Server`, malformed body → `Serialization` |
| `SqlDelightFavoriteProductsStorageTest` | 2 | Persistence round-trip, ordering, and empty-state behavior |
| `FavoritesRepositoryImplTest` | 6 | Restore on start, add/remove round-trip, ordering, per-product observation |
| `GetProductsUseCaseTest`, `GetProductDetailsUseCaseTest`, `GetFavoritesUseCaseTest`, `IsFavoriteUseCaseTest`, `ToggleFavoriteUseCaseTest` | 9 | One test class per use case; each delegates to its repository interface with the right arguments |
| `ProductsViewModelTest` | 12 | First page, append + cursor, exhaustion, search debounce, paging uses the active query, failure + retry, favorites, concurrent load-more guard, load-more error path, cancellation recovery |
| `ProductDetailsViewModelTest` | 4 | Load, failure, favorite toggle, toggle before load |
| `FavoritesViewModelTest` | 4 | Emission, empty state, reactive removal |

Tests use **hand-written fakes** rather than a mocking library: fakes compile on
every KMP target, while most mocking libraries are JVM-only and would keep these
tests out of `commonTest`.

The same 61 tests also run unmodified on the iOS simulator target:

```bash
./gradlew :shared:iosSimulatorArm64Test
```

Platform-specific test doubles (e.g. the in-memory SQLDelight driver used in
`SqlDelightFavoriteProductsStorageTest`) are provided via `expect`/`actual` —
`androidHostTest` supplies a JDBC driver, `iosTest` supplies SQLDelight's
native in-memory driver — so the ViewModel and repository logic under test is
identical on both platforms.

Three production bugs were found by these tests and fixed:

1. **Paging used the live keystroke query** instead of the one the visible page
   was loaded with, mixing results from two searches.
2. **Error classification** — non-2xx responses and malformed bodies both fell
   through to `CatalogError.Unknown` until `expectSuccess` and
   `ContentConvertException` handling were added.
3. **Concurrent pagination** — `onLoadMore`'s guard read `isLoadingMore` from
   the UI state, which is only set once the coroutine body runs. Two scroll
   callbacks in the same frame both passed the check and appended the same page,
   producing duplicate ids (a crash for a keyed `LazyColumn`). The guard is now
   a field set synchronously before `launch`. The regression test uses a
   `StandardTestDispatcher` — an unconfined one runs the body eagerly and hides
   the race entirely.

### 2. Android unit tests

```bash
./gradlew :androidApp:testDebugUnitTest
```

`ErrorMessagesTest` (3 plain JUnit tests) covers the `ErrorMessage` → `UiText`
string-resource mapping.

### 3. Instrumented Compose UI tests

```bash
./gradlew :androidApp:connectedDebugAndroidTest
```

Runs on a connected Android device or emulator to test full Compose interactions:
- Search text entry, debounce triggering, and list updates.
- Navigation transitions between list, details, and favorites.
- Favorite toggling.

## Multiplatform (KMP) Design & iOS Readiness

Per the brief for this Senior Android role, the user interface is built intentionally and exclusively for **Android with Jetpack Compose**. No separate iOS UI application bundle is included to avoid brittle simulator runtime/Xcode version discrepancies across reviewer environments.

However, the architecture strictly adheres to **Kotlin Multiplatform (KMP)** best practices:

* **`:shared` declares real native targets:** `iosArm64` and `iosSimulatorArm64` compile cleanly via `./gradlew :shared:compileKotlinIosArm64`. This is the mechanical proof that `commonMain` contains zero Android-specific or JVM leaks.
* **The full test suite runs on iOS, not just Android:** `./gradlew :shared:iosSimulatorArm64Test` executes the same `commonTest` suite — including all three ViewModel test classes — on the actual iOS Kotlin/Native simulator runtime. All 61 tests pass there with identical results to `testAndroidHostTest`, so the ViewModels aren't just assumed to be iOS-compatible by inspection; they're verified to run and behave correctly on iOS's own runtime.
* **Complete layer reuse:** All business logic, use cases, domain entities, Ktor remote networking, local persistence, and ViewModels live in `commonMain`.
* **Platform-specific engines decoupled:** `iosPlatformModule` binds the Apple Darwin HTTP engine and SQLDelight's `NativeSqliteDriver` for iOS, in parallel to OkHttp and `AndroidSqliteDriver` in `androidPlatformModule`.
* **Consuming from iOS:** If an iOS frontend were added, it would only need SwiftUI views observing the shared ViewModels (e.g., using SKIE or standard StateFlow wrappers) and calling the exposed action methods. Error messages resolve through a platform-neutral `ErrorMessage` enum that maps to `Localizable.strings` on iOS just as it maps to `strings.xml` on Android.

## Tech stack

| Concern | Choice |
| --- | --- |
| Multiplatform | Kotlin 2.4.10, AGP 9.2.1, Gradle 9.4.1 |
| UI | Jetpack Compose (Material 3) |
| Networking | Ktor 3.5.2 (OkHttp on Android, Darwin on iOS) |
| Serialization | kotlinx.serialization |
| DI | Koin 4.0.2 |
| Persistence & Offline | SQLDelight 2.0.2 (SQLite: AndroidSqliteDriver / NativeSqliteDriver) |
| Navigation | Navigation Compose, type-safe routes |
| Images | Coil 3 |
| Testing | kotlin.test, kotlinx-coroutines-test, Ktor MockEngine, Compose UI Test |

### Local persistence with SQLDelight

SQLDelight (the candidate's choice of persistence technology) backs favorites,
the only data the brief asks to be persisted:
- **Favorites persistence:** Favorites are stored in SQLite using `SqlDelightFavoriteProductsStorage`, preserving insertion order and supporting seamless offline favorites management.
- **Platform SQLite drivers:** `AndroidSqliteDriver` on Android and `NativeSqliteDriver` on iOS are injected via Koin platform modules, keeping all queries and database logic in `commonMain`.

The product catalog itself (list, search, details) is **not** cached locally —
`ProductRepositoryImpl` talks to the network only, since the brief scopes
persistence to favorites.

## Project layout

```
shared/src/commonMain/kotlin/com/landoulsi/catalog/shared/
├── core/           DataResult, CatalogError, DispatcherProvider
├── domain/         model · repository interfaces · usecase
├── data/           remote (Ktor, DTOs) · mapper · repository impls · local
├── presentation/   ViewModels + UiState per screen
└── di/             Koin module definitions
shared/src/androidMain/…  OkHttp + AndroidSqliteDriver bindings, androidPlatformModule
shared/src/iosMain/…      Darwin + NativeSqliteDriver bindings, iosPlatformModule, initKoinIos
shared/src/commonTest/…   Unit tests + fakes

androidApp/src/
├── main/
│   ├── kotlin/…/ui/           products · details · favorites · common · theme
│   ├── kotlin/…/navigation/   type-safe NavHost
│   └── res/values/            strings.xml (translatable) · dimens.xml · themes.xml
├── test/                      Android unit tests
└── androidTest/               Compose UI instrumented integration tests
```
