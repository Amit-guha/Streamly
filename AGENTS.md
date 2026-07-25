# AGENTS.md
# Streamly Android Project

This file is the single source of truth for all AI coding assistants.

`CLAUDE.md`, `.cursor/rules`, `.codex/instructions.md`, and `.antigravity/rules` are
symlinks to this file — never edit them directly or let them drift into separate copies.

---
# Project Goal

Build the Streamly application following the provided home assignment.
The focus is:
- Clean Architecture
- Kotlin
- Jetpack Compose
- MVVM + MVI
- Kotlin Coroutines + Flow
- Hilt
- Navigation 3
- Ktor
- Media3
- Offline-first mindset
- Production-quality code


# General Rules

Never use GlobalScope.
Never suppress warnings without explaining why.
Never duplicate business logic.
Always prefer immutable models.
Always prefer StateFlow over LiveData.
Use Kotlin best practices.
Always write readable code.
Keep functions short.
Keep classes focused.
Avoid unnecessary abstraction.

---

# Architecture
Always follow:

Presentation
↓
Domain
↓
Data

Presentation must never know Data layer.
Repositories belong in Domain.
Implementations belong in Data.
Dependency Injection through Hilt.
No direct API call from ViewModel.
No business logic inside UI.
Domain layer must be pure Kotlin — no Android or framework imports (no `android.*`, no `Context`, no `@Composable`, no framework annotations).

---

## MVI Rules
Every screen must define:
- UiState
- UiIntent
- UiEffect
- ViewModel

The ViewModel must:
- Extend `MVIViewModel<UiState, UiIntent, UiEffect>`.
- Handle all user actions inside `onIntent()`.
- Never expose mutable state.
- Use `_state.update { ... }` for state updates.
- Use `sendEffect()` for one-time UI events such as navigation, snackbars, and toasts.
- Keep `onIntent()` as the single entry point for user interactions.

---

# UI
Use Jetpack Compose for all UI.
No XML layouts, no Fragments.
UI must be stateless.
ViewModel owns state.
State must be immutable.
Events should be modeled using Intent.
One UiState per screen.

Prefer:
StateFlow instead of mutableStateOf
unless local Compose state is required.

## Adaptive Layouts
Build every screen with `WindowSizeClass` from the start.
Never assume a single fixed phone width.
Layouts must hold up on foldables and tablets, not just handsets.
Derive layout decisions (single-pane vs multi-pane, column counts, padding) from the window size class, not hardcoded breakpoints.

## Previews
Every `<Feature>Screen` must have `@Preview` composables for 3 device sizes: mobile, foldable, and tablet.
Use `@Preview(device = Devices.PHONE)`, `@Preview(device = Devices.FOLDABLE)`, and `@Preview(device = Devices.TABLET)` (or equivalent explicit `widthDp`/`heightDp`) to verify the layout at each `WindowSizeClass` breakpoint.
Preview with realistic sample state, not empty/placeholder data.

---

## Base ViewModel Convention
All feature ViewModels must inherit from the project's base `MVIViewModel<S, I, E>`.
Do not inherit directly from `ViewModel` unless explicitly instructed.

Every ViewModel must:
- Extend `MVIViewModel<UiState, UiIntent, UiEffect>`.
- Provide the initial `UiState` to the base class.
- Implement `onIntent(intent: UiIntent)`.
- Update UI state only through `_state.update { ... }`.
- Emit one-time events only through `sendEffect()`.
- Expose immutable `StateFlow` and `SharedFlow` from the base class.
- Never expose `MutableStateFlow` or `MutableSharedFlow` outside the ViewModel.
- Keep all business logic inside the ViewModel or delegated UseCases.

Standard structure:
- UiState
- UiIntent
- UiEffect
- ViewModel : MVIViewModel<UiState, UiIntent, UiEffect>

---

# UseCase Rules
Never access a Repository directly from a ViewModel.
ViewModels must communicate only with UseCases.
Every feature should have one or more UseCases, even if the logic is simple.
Each UseCase must have a single responsibility.
Use constructor injection for dependencies.

Naming convention:
- GetHomeFeedUseCase
- GetShortsUseCase
- GetVideoDetailsUseCase
- DownloadVideoUseCase
- DeleteDownloadUseCase
- SignOutUseCase

UseCases belong in:
feature/<feature>/domain/usecase/

---

# Core Module Rules
The `core` module contains only shared infrastructure used across multiple features. Never place feature-specific business logic inside `core`.

```
core/
├── common/
│   ├── base/
│   ├── extension/
│   ├── util/
│   ├── constant/
│   └── enum/
│
├── domain/
│   └── storage/
│       └── datastore/
│
├── data/
│   └── storage/
│       └── datastore/
│
├── network/
│   ├── api/
│   ├── interceptor/
│   ├── serializer/
│   └── model/
│
├── local/
│   ├── database/
│   ├── dao/
│   ├── entity/
│   └── migration/
│
├── designsystem/
│   └── component/
│
├── navigation/
│
└── di/
```



---

## common/
Shared utilities used across the application.

### Contains
- `base/` → Base classes shared by every feature (e.g. `MVIViewModel`).
- `extension/` → Kotlin extension functions.
- `util/` → Helper and utility classes.
- `constant/` → Shared constants.
- `enum/` → Shared enums.

### Rules
- Only reusable code belongs here.
- Never place feature-specific code in `common`.

### base/
Contains `MVIViewModel<S, I, E>`, the abstract base every feature ViewModel extends.

**Rules**
- Pure Kotlin + `androidx.lifecycle.ViewModel` only — no feature or Compose imports.
- Exposes `state: StateFlow<S>` (backed by `MutableStateFlow`) and `effect: SharedFlow<E>` (backed by `MutableSharedFlow`) — never expose the mutable versions.
- Constructor takes the initial `UiState`.
- Declares `abstract fun onIntent(intent: I)` as the single entry point for user actions.
- Provides a `protected fun sendEffect(effect: E)` that launches on `viewModelScope` to emit one-time events.
- Provides a `protected fun updateState(reducer: S.() -> S)` (or exposes `_state` as `protected`) so subclasses update state only via `_state.update { ... }`.
- No business logic lives in the base class — it only wires state/intent/effect plumbing.

---

## domain/storage/
Shared storage contracts.

### Rules
- Contains interfaces only.
- No implementations.
- No Android framework dependencies.

---

## data/storage/
Shared storage implementations.

### Rules
- Implements interfaces defined in `core/domain`.
- Keep implementation details hidden from features.
- No business logic.

---

## network/
Shared networking infrastructure.

### Contains
- API interfaces
- Interceptors
- Serialization configuration
- Shared network models

### Rules
- Shared networking only.
- Never place feature repositories or business logic here.

---

## local/
Shared Room database infrastructure.

### Contains
- Database
- DAO
- Entity
- Migrations

### Rules
- All Room-related classes belong here.
- Do not place repositories or business logic here.
- Features should access the database through their own repositories or local data sources.

---

## designsystem/
Reusable Compose components shared across features.

### Rules
- Only reusable UI components belong here.
- Feature-specific components belong inside:

```
feature/<feature>/presentation/component/
```

---

## navigation/
Application-level navigation only.

### Rules
- Keep only root navigation and shared navigation utilities.
- Feature navigation belongs inside:

```
feature/<feature>/presentation/navigation/
```


---
## di/

Shared dependency injection.
### Rules
- Register only shared dependencies used across the application.
- Do not register feature-specific dependencies.
- Feature DI belongs inside:

```
feature/<feature>/di/
```

---









# Core Principles
- `core` contains only shared infrastructure.
- `core/domain` defines shared contracts.
- `core/data` implements shared contracts.
- Promote code to `core` only when it is reused by multiple features.
- Keep business logic inside the corresponding feature.
- Never allow `core` to become a dumping ground for feature-specific code.

---







# Feature Structure
Every feature must follow this structure.
```
feature/
└── feature_name/
    ├── data/
    │   ├── datasource/
    │   │   ├── remote/
    │   │   └── local/
    │   ├── dto/
    │   ├── mapper/
    │   └── repository/
    │
    ├── domain/
    │   ├── model/
    │   ├── repository/
    │   └── usecase/
    │
    ├── presentation/
    │   ├── <Feature>Screen.kt
    │   ├── <Feature>ViewModel.kt
    │   │
    │   ├── contract/
    │   │   ├── <Feature>ScreenState.kt
    │   │   ├── <Feature>Intent.kt
    │   │   └── <Feature>Effect.kt
    │   │
    │   ├── navigation/
    │   │   └── <Feature>Route.kt
    │   │
    │   └── component/
    │
    └── di/
```


## Presentation Rules
### screen/
### <Feature>ScreenRoute.kt
`<Feature>ScreenRoute.kt` is the entry point of a feature and connects the `ViewModel` to the UI.

**Responsibilities**
- Obtain the `ViewModel` using `hiltViewModel()`.
- Collect `Effect` using `LaunchedEffect`.
- Handle navigation and one-time UI events (Toast, Snackbar, Dialog, etc.).
- Pass `uiState` and `onIntent` to `<Feature>Screen`.

**Rules**
- Collect `Effect` only in `ScreenRoute`.
- Handle navigation only in `ScreenRoute`.
- Keep `<Feature>Screen` stateless.
- Keep business logic out of `ScreenRoute`.

Example
```
@Composable
fun <Feature>ScreenRoute(
    destination: (NavigationDestination) -> Unit,
    onBackPress: () -> Unit,
    viewModel: <Feature>ViewModel = hiltViewModel(),
) {
    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
        
        }
    }
}

@Composable
    <Feature>Screen(
        uiState = viewModel.uiState,
        onBackPress = onBackPress,
        onIntent = viewModel::onIntent,
    )
```


### viewmodel/
- Contains ViewModels only.
- Executes UseCases.
- Updates ScreenState.
- Emits Effects.

### contract/
Contains the MVI contract.
- `ScreenState`
- `Intent`
- `Effect`

### navigation/
Contains feature navigation.
- Route
- Navigation wiring

### component/
Contains reusable components used only within the feature.
Move shared components to `core/designsystem/component`.

---

## Domain Rules
### model/
Plain immutable Kotlin data classes representing the feature's business concepts.
Never reference DTOs, Entities, or any data-layer type from a domain model.

### repository/
Interface only — no implementation.
Methods return domain models via `suspend fun` or `Flow`, never DTOs/Entities.

### usecase/
See `UseCase Rules` above — one responsibility per UseCase, constructor-injected repository.

### Rules
- Domain never imports Android/framework types, Ktor, Room, or DTO/Entity classes.
- Domain has no knowledge of where data comes from (network vs local vs cache).

---

## Data Rules
### datasource/remote/
Always required — every feature has at least one remote data source wrapping its Ktor API calls.

### datasource/local/
Only required when the feature needs offline persistence or caching (e.g. Downloads, offline-first feed).
Skip this folder entirely for features with no persistence need (e.g. static onboarding copy) — do not create an empty/unused local data source for structural symmetry.

### dto/
Only required when the remote response shape differs from the domain model.
If the API response already matches the domain model 1:1, skip the DTO and deserialize directly into the domain model — don't create a pass-through DTO just to have one.

### mapper/
Required whenever a DTO or Entity exists, to convert it to a domain model.
Never create a mapper with nothing to map (no DTO/Entity in the feature).

### repository/
`<Feature>RepositoryImpl` implements the domain repository interface.
The only layer allowed to combine/coordinate remote and local data sources.

### Rules
- Generate only the files a feature actually uses — an empty `dto/`, `local/`, or `mapper/` folder created "for completeness" is a smell, not a convention.
- When in doubt, start with remote-only + domain model; add DTO/mapper/local later only when the API shape or offline requirement demands it.

---






# Naming
ViewModel
HomeViewModel

State
HomeUiState

Intent
HomeIntent

Effect
HomeEffect

Repository
VideoRepository

RepositoryImpl
VideoRepositoryImpl

Use descriptive names.

---





# Dependency Injection
Use Hilt.
Constructor injection whenever possible.


---

# Dependencies (Gradle)
Declare every dependency in `gradle/libs.versions.toml` — `[versions]`, `[libraries]`, `[plugins]`.
Never hardcode a group/artifact/version string directly inside a `build.gradle.kts` file; reference it via `libs.*` from the version catalog.
Reuse an existing version entry instead of duplicating the same version string under a new key.
Group related libraries into a `[bundles]` entry when a feature always pulls them together (e.g. Compose, Media3).

---






# Coroutines
Use viewModelScope.
Repository functions should be suspend or Flow.
Never block Main thread.
Handle cancellation correctly.
Use Dispatchers.IO only where appropriate.
No RxJava — Coroutines + Flow end to end.

---




# Error Handling
Never swallow exceptions.
Return Result where appropriate.
Convert network errors into domain models.
UI should never receive raw exceptions.

---



# Testing
Write unit tests where they matter most: UseCases, ViewModels (state/intent transitions), and mappers.
ViewModels must be testable without Android framework dependencies — constructor-inject everything, no static/singleton access.
Use fake/in-memory repositories for ViewModel tests, not mocks of the framework.
Don't chase coverage for its own sake; prioritize business logic and state transitions over trivial getters/DI wiring.

---



# Networking
Use Ktor.
Create:
NetworkModule
ApiService

---



# Models
DTO
↓
Mapper
↓
Domain
↓
UI Model
Never skip layers.



---


# Compose
Prefer:
LazyColumn
derivedStateOf
rememberSaveable
collectAsStateWithLifecycle()

Avoid:
remember for business state
Huge composables



---

# Strings
Never hardcode user-facing text inside a Composable or ViewModel.
All user-facing strings belong in `res/values/strings.xml` accessed via `stringResource()` in Compose.
Never concatenate translatable strings at runtime — use string resource format args instead.
Constants that are not user-facing (log tags, keys, route names) do not belong in `strings.xml`; keep those in `core/common/constant`.

---

# Colors
Never hardcode color values (hex codes, `Color(0xFF...)`) inside a Composable.
Define colors in `Color.kt` and wire them into `LightColorScheme` / `DarkColorScheme` inside `Theme.kt` (`StreamlyTheme`), following the existing `ui/theme/Theme.kt` setup.
Access colors only through `MaterialTheme.colorScheme.*` in Composables, never by referencing raw `Color` constants directly.
`LightColorScheme` and `DarkColorScheme` in `Theme.kt` must use the same color values — the app uses one fixed color scheme regardless of system light/dark setting.
Disable dynamic color (`dynamicColor = false`) in `StreamlyTheme` so the fixed scheme is never overridden by system wallpaper colors.

---


# Navigation
Use Navigation 3.
Navigation events come from ViewModel.
Do not navigate directly from business logic.

---




# Media3
Single shared `ExoPlayer` instance for the normal-video player; create once, reuse across config changes and navigation, release only on final screen exit.
Shorts feed: separate pooled/pager player strategy — no more than 1–2 `ExoPlayer` instances alive at once, matching visible + adjacent item. Document the strategy in code comments if non-obvious.
Lifecycle: pause on backgrounding, resume on return, release on screen exit.
No leaked players, no audio bleeding between screens.
Downloads: use Media3 `DownloadManager`/offline module. Real progress state, not simulated. Completed downloads play from local storage.
Delivery format: HLS only, via `media3-exoplayer-hls`, against `.m3u8` streams. No local MP4-only shortcuts. Adaptive bitrate switching must work out of the box.

---


# Code Style
Prefer extension functions.
Prefer data class.
Use explicit visibility.

---



# Before Writing Code
Always:
1. Understand existing architecture.
2. Search for reusable components.
3. Avoid duplication.
4. Follow project style.
5. Explain architectural decisions when introducing new patterns.

---



# When Creating New Feature
Always generate:
Feature
UiState
Intent
Effect
ViewModel
Repository
RepositoryImpl
UseCase
Composable
Navigation
DI Module (if required)



---

# Git
Keep commits focused.
Never modify unrelated files.
Respect existing formatting.
Commits authored with agent assistance must include a `Co-Authored-By:` trailer (or equivalent), so the agentic workflow is visible in history.

---






# Response Style
Before coding:
Explain the plan briefly.

While coding:
Keep changes minimal.
Never output unnecessary code.

---