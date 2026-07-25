# Agent Changelog

Log of AI-agent-assisted work on Streamly, in chronological order. Each entry is
added at the time the work happens (same commit or the one right after), not
retrofitted at the end.

Format:
```
## YYYY-MM-DD
### Agent
<agent name>

### Commit
<commit-hash or "pending">

### Task
<short task title>

### Prompt
<what was asked>

### Changes
- <bullet summary of changes>

### Files
- <files/areas touched>
```

---

## 2026-07-25
### Agent
Claude Code

### Commit
42dd94f

### Task
Configure AI workflow.

### Prompt
Create AGENTS.md as the single source of truth and configure symlinks for Claude, Cursor, and Codex.

### Changes
- Added AGENTS.md
- Added CLAUDE.md symlink
- Added .cursor/rules symlink
- Added .codex/instructions.md symlink
- Added .antigravity/rules symlink

### Files
- AGENTS.md
- CLAUDE.md
- .cursor/rules
- .codex/instructions.md
- .antigravity/rules

--------------------

## 2026-07-25
### Agent
Claude Code

### Commit
60a0b55

### Task 1
Create the agent changelog file.

### Prompt
"Create a new file at `docs/AGENTS_CHANGELOG.md`. This file will serve as a changelog for all AI-agent-assisted work on Streamly. 

### Changes
- Created docs/AGENTS_CHANGELOG.md 
- Defined the entry format/template used by this file

### Files
- docs/AGENTS_CHANGELOG.md

----------------------

### Task 2
Update AGENTS.md with additional rules.

### Prompt
"add rule for String, Colors, Previews" / "keep color code in Color file  and use through Material Theme" / " 3 preview every screen including foldable device" / "Light and Dark theme colors will always be the same." / "set features domain or data rules inside Agents.md" (added Domain/Data Rules, kept DTO/local/mapper conditional) / "add base inside core/ common/ "Add a rule that all dependencies, plugins, and versions must be managed  through gradle/libs.versions.toml"

### Changes
- Added `Strings` rule — no hardcoded user-facing text, use `stringResource()`
- Added `Colors` rule — colors defined in Color.kt/Theme.kt, accessed via `MaterialTheme.colorScheme`, light/dark schemes kept identical, dynamic color disabled
- Added `Previews` rule — 3 `@Preview`s per screen (mobile, foldable, tablet)
- Added `Domain Rules` and `Data Rules` subsections under Feature Structure — remote datasource always required; DTO/mapper/local datasource only when actually needed
- Added `core/common/base/` to the core tree and documented `MVIViewModel` base class rules
- Added `Dependencies (Gradle)` section — all deps declared via `gradle/libs.versions.toml`, no hardcoded versions in build files

### Files
- AGENTS.md

--------------------

## 2026-07-25
### Agent
Claude Code

### Commit
39540cb

### Task
Configure project dependencies through the version catalog.

### Changes
- Added `gradle/libs.versions.toml` entries (versions, libraries, plugins, bundles) for Hilt, Ktor, Room, Media3, Navigation 3, Kotlin Coroutines, kotlinx.serialization, AndroidX DataStore, Lifecycle (runtime-compose, viewmodel-compose), Compose Material3 WindowSizeClass, and Coil
- Grouped `media3`, `ktor`, `room`, `navigation3`, and `coil` libraries into `[bundles]` entries
- Applied `kotlin-serialization`, `ksp`, and `hilt-android` plugins in the root and `app` `build.gradle.kts`, all referenced via `libs.plugins.*`
- Wired the new dependencies into `app/build.gradle.kts` via `libs.*`/`libs.bundles.*`, including `ksp(libs.hilt.android.compiler)` and `ksp(libs.androidx.room.compiler)`
- Added `kotlinx-coroutines-test` to `testImplementation`
- Added `android.disallowKotlinSourceSets=false` to `gradle.properties` as a workaround for google/ksp#2729 (KSP vs. AGP 9 built-in Kotlin support)

### Files
- gradle/libs.versions.toml
- build.gradle.kts
- app/build.gradle.kts
- gradle.properties
- .gitignore

--------------------

## 2026-07-25
### Agent
Claude Code

### Commit
501346d

### Task
Wire up the shared `core/` infrastructure — base MVI ViewModel, DataStore-backed preferences, Navigation 3, Ktor, and Room — plus a first `home`/`profile` feature pair to prove the Nav3 + Hilt ViewModel scoping end to end.

### Changes
- Added `core/common/base/MVIViewModel.kt` — the base class every feature ViewModel extends (`state`/`effect` exposed read-only, `sendEffect` launches on `viewModelScope`, `onIntent` abstract)
- Added `core/domain/storage/datastore/AppPreferences.kt` (interface) and `core/data/storage/datastore/AppPreferencesImpl.kt` (DataStore-backed impl) for `isLoggedInFlow` / `isLoggedIn()` / `setLoggedIn()` / `clearSession()`; wired via `core/di/DataStoreModule.kt` and `core/di/StorageModule.kt`
- Added `StreamlyApp.kt` (`@HiltAndroidApp`) since none existed yet, and wired it into the manifest; added `@AndroidEntryPoint` to `MainActivity`
- Added Navigation 3 setup: `core/navigation/NavigationDestination.kt` (typealias for `NavKey`), `AppNavigator.kt` (back-stack holder), `AppNavGraph.kt` (wraps `NavDisplay` with `rememberViewModelStoreNavEntryDecorator` so `hiltViewModel()` scopes per back-stack entry); `StreamlyNavHost.kt` at the app root aggregates every feature's `*Entries` extension (kept out of `core` since `core` can't depend on features)
- Added `feature/home` and `feature/profile` (full MVI: `UiState`/`Intent`/`Effect`/`HiltViewModel`, stateless `Screen` + 3 `@Preview`s, `<Feature>ScreenRoute` living in `<Feature>Screen.kt`, `NavKey` + `EntryProviderScope` extension living in `navigation/<Feature>Route.kt`) as the first working example of the documented feature-navigation pattern
- Added Ktor setup: `core/network/json/JsonProvider.kt`, `core/network/api/ApiService.kt` (generic `get`/`post`/`put`/`delete` over `HttpClient`), `core/di/NetworkModule.kt` (OkHttp engine, `ContentNegotiation`, `Logging` gated on `BuildConfig.DEBUG`, `HttpTimeout`, `defaultRequest`, `expectSuccess = true`)
- Added the `Result`/`Status` pattern for repository → ViewModel state: `core/common/enum/Status.kt`, `core/common/util/Result.kt`, `core/common/util/ResultFlow.kt` (`resultFlow { }` emits `LOADING` then `SUCCESS`/`ERROR`, catching exceptions so raw exceptions never reach a ViewModel)
- Added Room setup: `core/local/entity/DownloadEntity.kt`, `core/local/dao/DownloadDao.kt`, `core/local/database/StreamlyDatabase.kt` (version 1, no migrations yet), `core/di/DatabaseModule.kt`; `core/common/enum/DownloadStatus.kt` and `core/common/constant/{NetworkConstants,DatabaseConstants}.kt`
- Enabled `buildFeatures.buildConfig = true` in `app/build.gradle.kts` (needed for the `BuildConfig.DEBUG` check in `NetworkModule`)
- Updated AGENTS.md: reworded the Presentation Rules navigation/`<Feature>Route.kt` section and its example to match the shape actually implemented, and updated the `core/network/` tree entry from `serializer/` to `json/`

### Files
- app/src/main/java/com/example/streamly/StreamlyApp.kt
- app/src/main/java/com/example/streamly/StreamlyNavHost.kt
- app/src/main/java/com/example/streamly/MainActivity.kt
- app/src/main/java/com/example/streamly/core/common/base/MVIViewModel.kt
- app/src/main/java/com/example/streamly/core/common/constant/{DataStoreConstants,NetworkConstants,DatabaseConstants}.kt
- app/src/main/java/com/example/streamly/core/common/enum/{Status,DownloadStatus}.kt
- app/src/main/java/com/example/streamly/core/common/util/{Result,ResultFlow}.kt
- app/src/main/java/com/example/streamly/core/domain/storage/datastore/AppPreferences.kt
- app/src/main/java/com/example/streamly/core/data/storage/datastore/AppPreferencesImpl.kt
- app/src/main/java/com/example/streamly/core/di/{DataStoreModule,StorageModule,NetworkModule,DatabaseModule}.kt
- app/src/main/java/com/example/streamly/core/navigation/{NavigationDestination,AppNavigator,AppNavGraph}.kt
- app/src/main/java/com/example/streamly/core/network/json/JsonProvider.kt
- app/src/main/java/com/example/streamly/core/network/api/ApiService.kt
- app/src/main/java/com/example/streamly/core/local/entity/DownloadEntity.kt
- app/src/main/java/com/example/streamly/core/local/dao/DownloadDao.kt
- app/src/main/java/com/example/streamly/core/local/database/StreamlyDatabase.kt
- app/src/main/java/com/example/streamly/feature/home/**
- app/src/main/java/com/example/streamly/feature/profile/**
- app/src/main/AndroidManifest.xml
- app/src/main/res/values/strings.xml
- app/build.gradle.kts
- AGENTS.md

--------------------------------------------------------------------------------------------------------------

## 2026-07-26
### Agent
Claude Code

### Commit
pending

### Task
Add the sign-in flow: Authentication, SignInWithEmail, and Splash-based session routing.

### Changes
- Added `feature/auth/authentication` — the sign-in screen (Continue with Google, Sign in with email, Continue as guest);
- Added `feature/auth/signinwithemail` — name + email capture screen; 
- Added `feature/splash` — Splash is now a real screen (branded gradient + `FullScreenLoadingIndicator`), always the start destination;
- Extended `AppPreferences`/`AppPreferencesImpl` with `userNameFlow`/`userEmailFlow`/`saveUserProfile()` so the email flow's captured name/email can be shared across features via the existing DataStore-backed store.
- Updated `feature/profile` to show the real captured name/email (via a new `ObserveUserProfileUseCase`), falling back to "Guest" / "No email added" for the Google/guest paths.
- Added `core/designsystem/component/FullScreenLoadingIndicator.kt` — a reusable, parameterized loading overlay (`indicatorColor`, `backgroundColor`, `isBlockingInteraction`, `zIndex`) that blocks touches to whatever's behind it.
- Fixed the theme to match AGENTS.md's own rules (previously never actually applied): `dynamicColor = false`, identical Light/Dark color schemes, and added the Streamly blue brand palette used by Authentication/Splash.
- Migrated `hiltViewModel()` off the deprecated `androidx.hilt.navigation.compose` to `androidx.hilt.lifecycle.viewmodel.compose` (new `hilt-lifecycle-viewmodel-compose` artifact, pinned to 1.3.0 to avoid a transitive `compileSdk 37` requirement).
- Added `core/common/constant/AppConstants.kt` (`SPLASH_MIN_DISPLAY_DURATION`) and a matching `delay()` in `SplashViewModel` so the splash branding doesn't just flash by instantly.
- Added `README.md`.

### Files
- app/src/main/java/com/example/streamly/feature/auth/authentication/**
- app/src/main/java/com/example/streamly/feature/auth/signinwithemail/**
- app/src/main/java/com/example/streamly/feature/splash/**
- app/src/main/java/com/example/streamly/feature/profile/**
- app/src/main/java/com/example/streamly/feature/home/presentation/HomeScreen.kt
- app/src/main/java/com/example/streamly/core/domain/storage/datastore/AppPreferences.kt
- app/src/main/java/com/example/streamly/core/data/storage/datastore/AppPreferencesImpl.kt
- app/src/main/java/com/example/streamly/core/common/constant/{AppConstants,DataStoreConstants}.kt
- app/src/main/java/com/example/streamly/core/designsystem/component/FullScreenLoadingIndicator.kt
- app/src/main/java/com/example/streamly/core/navigation/AppNavigator.kt
- app/src/main/java/com/example/streamly/StreamlyNavHost.kt
- app/src/main/java/com/example/streamly/ui/theme/{Color,Theme}.kt
- app/src/main/res/values/strings.xml
- app/build.gradle.kts
- gradle/libs.versions.toml
- README.md
