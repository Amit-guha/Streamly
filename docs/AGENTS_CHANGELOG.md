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
5ecd2ed

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

--------------------------------------------------------------------------------------------------------------

## 2026-07-27
### Agent
Claude Code

### Commit
83b4f43

### Task
Refine the Home feature: extract reusable UI components, adopt shared design-system components, add an adaptive grid layout, and fix a few rule violations.

### Changes
- Extracted `HomeScreen`'s private sub-composables into `feature/home/presentation/component/`: `ErrorContent`, `CategoryChipsRow`, `VideoCard`, `HomeFeedContent`
- Home's feed now adapts to `WindowSizeClass`: single-column list on compact width (phone), `LazyVerticalGrid` with `GridCells.Adaptive` on medium/expanded width (foldable/tablet) — column count isn't hardcoded
- Moved `VIDEOS_ASSET_FILE_NAME` out of a local `private const val` in `HomeNetworkModule` into `core/common/constant/AppConstants`
- Integrate Whole Home Feed Feature 

### Files
- app/src/main/java/com/example/streamly/feature/home/presentation/HomeScreen.kt
- app/src/main/java/com/example/streamly/feature/home/presentation/component/**
- app/src/main/java/com/example/streamly/feature/home/di/HomeNetworkModule.kt
- app/src/main/java/com/example/streamly/core/designsystem/component/CircularCommonLoader.kt
- app/src/main/java/com/example/streamly/core/common/constant/AppConstants.kt
- app/src/main/java/com/example/streamly/feature/splash/presentation/SplashScreen.kt

--------------------------------------------------------------------------------------------------------------

## 2026-07-27
### Agent
Claude Code

### Commit
83b4f43

### Task
Build the Player (normal video) feature end to end, and introduce presentation-layer UI models so Presentation stops depending on domain models directly.

### Changes
- Added `feature/player`: `PlayerRepository`/`PlayerRepositoryImpl` (reuses `HomeRepository`'s catalog for video details + up-next instead of duplicating the fetch), `GetVideoDetailsUseCase`, full MVI contract (`PlayerUiState`/`PlayerIntent`/`PlayerEffect`), `PlayerViewModel`, and `PlayerScreen` split into `presentation/component/` (`PlayerSurface`, `VideoMetadataSection`, `ActionButtonsRow`, `UpNextItems`, `UpNextVideoItem`)
- Added `PlayerController`/`Media3PlayerController` (`feature/player/di/`) — wraps Media3's `Player` behind a small, fakeable contract (`play`/`resume`/`pause`/`setMuted`/`release`/`isPlaying`) instead of the ViewModel driving Media3 APIs directly
- Single shared `ExoPlayer` instance: created once per `PlayerViewModel`, reused across up-next switches (handled entirely via intent, no new nav entry/ViewModel per video) and configuration changes, released only in `onCleared()`
- Lifecycle-correct pause/resume: `wasPlayingBeforeSystemPause` bookkeeping so a rotation's pause/resume cycle only resumes playback if it was actually playing before (fixes an earlier bug where rotation force-resumed a paused video)
- Added `VideoUiModel` + `toUiModel()` mapper in both `feature/home/presentation/model/` and `feature/player/presentation/model/`; `HomeUiState`/`HomeIntent`/`HomeEffect`/`PlayerUiState`/`PlayerIntent` and their composables now use it instead of the domain `Video` model directly
- Fixed player audio continuing briefly after pressing back: `AppNavGraph`'s exit transition keeps both screens composed during the animation, so `PlayerScreenRoute` now pauses immediately via `BackHandler` (covers both the in-app back icon and the system back button/gesture) instead of waiting for `onCleared()` at the end of the transition
- Fixed the resulting screen-overlap looking messy during that transition: `PlayerScreenRoute` swaps to a blank `Surface` the instant back is requested, so the transition has nothing dense to double-expose against; replaced the plain crossfade in `AppNavGraph` with a Material "shared axis" slide+fade
- `videos.json` expanded from 2 to 10 entries (reusing the two HLS streams with distinct metadata) so Home's grid and Player's up-next list have real variety

### Files
- app/src/main/java/com/example/streamly/feature/player/**
- app/src/main/java/com/example/streamly/feature/home/presentation/model/VideoUiModel.kt
- app/src/main/java/com/example/streamly/feature/home/presentation/{HomeViewModel,HomeScreen}.kt
- app/src/main/java/com/example/streamly/feature/home/presentation/contract/{HomeUiState,HomeIntent,HomeEffect}.kt
- app/src/main/java/com/example/streamly/feature/home/presentation/component/{VideoCard,HomeFeedContent}.kt
- app/src/main/java/com/example/streamly/core/navigation/AppNavGraph.kt
- app/src/main/java/com/example/streamly/core/designsystem/LocalWindowSizeClass.kt
- app/src/main/java/com/example/streamly/MainActivity.kt
- app/src/main/java/com/example/streamly/StreamlyNavHost.kt
- app/src/main/assets/videos.json
- app/build.gradle.kts
- gradle/libs.versions.toml

--------------------------------------------------------------------------------------------------------------

## 2026-07-27
### Agent
Claude Code

### Commit
9e2b883

### Task
Build the Shorts feature: full-screen vertical pager with pooled Media3 playback.

### Prompt
Build a Facebook/Instagram-style full-screen vertical Shorts pager — data from `assets/shorts.json`, autoplay on the visible item, center play/pause, 5s seek forward/backward, a draggable progress bar, mute, and non-functional like/comment/share stubs — using a pooled player strategy so no more than 1-2 `ExoPlayer` instances are ever alive at once.

### Changes
- Added `feature/shorts` end to end: domain `Short` model, `ShortsRepository`/`ShortsRepositoryImpl`, `GetShortsUseCase`, and a mock Ktor client reading `assets/shorts.json` (same pattern as `feature/home`)
- Full MVI: `ShortsUiState`/`ShortsIntent`/`ShortsEffect`, `ShortsViewModel`, `ShortsScreen` (`ShortsScreenRoute` + stateless `ShortsScreen`) built on `VerticalPager`
- Added `ShortsPlayerPool`/`Media3ShortsPlayerPool` (`feature/shorts/di/`) — an LRU-capped pool (max 2 live `ExoPlayer`s) that always holds `{visible, next}`, evicting the least-recently-used player on every page change; self-heals to the same window in either swipe direction, documented in code
- Built the player UI on `androidx.media3.ui.compose` (Compose-native Media3 APIs): `PlayerSurface` + `rememberPresentationState` for the video, `PlayPauseButton`/`SeekBackButton`/`SeekForwardButton` (wired to 5s increments) for center transport controls, `ProgressIndicator` state driving a draggable `Slider` seek bar; mute stays ViewModel-owned since it must persist across pooled player swaps, everything else is native player-driven with no ViewModel round trip
- Added components: `ShortPagerItem`, `ShortPlayerControls` (nullable `Player` with a static fallback row so it can be previewed), `ShortProgressBar`, `ShortActionRail` (like/comment/share stubs + functional mute), `ShortCaptionOverlay`, `ShortsErrorContent`
- Fixed a `StateFlow` conflation bug: the pool's page-0 player was created correctly but the UI never recomposed to pick it up, since re-dispatching `OnPageChanged(0)` produced a structurally-equal state; fixed with an explicit `playerGeneration` counter in `ShortsUiState`
- Added `AppConstants.SHORTS_ASSET_FILE_NAME` and `AppConstants.SHORTS_MAX_PAGER_WIDTH_DP` (caps the pager's width on foldables/tablets instead of stretching full-bleed)
- Added `assets/shorts.json` (8 entries); swapped the initial placeholder `videoUrl`s (a 30-minute Apple test-pattern loop and multi-minute movie streams) for a verified-live, genuinely short (~60s) HLS stream (`shaka-demo-assets/angel-one-hls`) after confirming reachability and duration directly


### Files
- app/src/main/java/com/example/streamly/feature/shorts/**
- app/src/main/java/com/example/streamly/feature/home/presentation/HomeScreen.kt
- app/src/main/java/com/example/streamly/feature/home/presentation/HomeViewModel.kt
- app/src/main/java/com/example/streamly/feature/home/presentation/contract/{HomeIntent,HomeEffect}.kt
- app/src/main/java/com/example/streamly/StreamlyNavHost.kt
- app/src/main/java/com/example/streamly/core/common/constant/AppConstants.kt
- app/src/main/assets/shorts.json
- app/src/main/res/values/strings.xml

--------------------------------------------------------------------------------------------------------------

## 2026-07-28
### Agent
Claude Code

### Commit
fbb106b

### Task
Build the Downloads feature end to end (real Media3 offline downloads, Player download button, Downloads screen)

### Prompt
Task: Implemented with Media3's DownloadManager/offline module - real progress and playback from local storage, not a fake progress bar. List of in-progress and completed downloads with real progress state; completed items play back from local storage; support removing a download.

### Changes
- Added `feature/downloads` end to end: `DownloadItem` domain model, `DownloadsRepository`/`DownloadsRepositoryImpl`, use cases (`GetDownloadsUseCase`, `GetDownloadStatusUseCase`, `DownloadVideoUseCase`, `DeleteDownloadUseCase`, `GetTotalStorageBytesUseCase`), full MVI (`DownloadsUiState`/`Intent`/`Effect`/`ViewModel`), `DownloadsScreen` (storage-used header via real `StatFs`, list with progress bar / green "Ready to play" / remove action, 3 previews), `DownloadsRoute`/`DownloadsNavKey`
- Added the real Media3 download engine: a shared `Cache`/`CacheDataSource.Factory` (`core/di/DownloadManagerModule.kt`) backing both the `DownloadManager` (writes) and `Media3PlayerController`'s playback `MediaSourceFactory` (reads), so a completed download plays from local storage with no special-casing
- Wired the Player screen's Download action pill (`ActionButtonsRow`): states for not-downloaded / downloading (indeterminate spinner, no percentage — matches YouTube, real progress is still tracked in Room underneath) / downloaded / failed; tapping while a download exists opens `DownloadOptionsBottomSheet` (single "Delete from downloads" row); tapping to start one shows a "Downloading…" snackbar with a "View" action that navigates to the Downloads screen
- Added a Downloads entry point icon in Home's `TopAppBar`, registered `downloadsEntries` in `StreamlyNavHost`
- Fixed two real bugs found via on-device testing: (1) empty `streamKeys` downloaded every HLS bitrate rendition (~700MB for a demo clip, exhausted emulator storage) — fixed by building the `DownloadRequest` through `DownloadHelper` with `forceLowestBitrate = true`, and forcing the same rendition selection during playback of a completed download so offline playback actually hits the cached segments instead of stalling on a network fetch for a different rendition; (2) `DownloadManager.Listener.onDownloadChanged` only fires on discrete state transitions, not continuously while `STATE_DOWNLOADING`, so progress looked frozen at 0% then jumped straight to done — fixed by adding an active polling loop (`Media3DownloadListener`, every 500ms via `currentDownloads`) alongside the listener
- Refactored the original monolithic `DownloadsRepositoryImpl` (which held a `Context`, a `CoroutineScope`, `DownloadManager.Listener`, and `DownloadHelper` request-building all in one class) into single-responsibility collaborators: `DownloadController`/`Media3DownloadController` (service dispatch), `DownloadRequestFactory` (`DownloadHelper` track selection), `Media3DownloadListener` (the one `DownloadManager.Listener` + polling loop, explicitly started from `StreamlyApp.onCreate()`), `StorageInfoProvider`/`AndroidStorageInfoProvider` (`StatFs`); `DownloadsRepositoryImpl` is now pure orchestration with no `@Singleton` (nothing depends on it being one instance now — the actual singleton scoping for consumers comes from `@Binds @Singleton` in `DownloadsModule`)
- Added an injectable dispatcher pattern instead of hardcoding `Dispatchers.X`: `core/common/base/dispatcher/Dispatcher.kt` (`@Dispatcher(AppDispatchers)` qualifier + `IO`/`DEFAULT`/`MAIN`/`MAIN_IMMEDIATE` enum) and `core/di/DispatchersModule.kt`; every call site now injects the specific dispatcher it needs
- Fixed the download-started snackbar rendering under the nav bar on a real device: added `.windowInsetsPadding(WindowInsets.systemBars)`, matching the pattern already used by `ShortsScreen`/`ShortPagerItem`

### Files
- app/src/main/java/com/example/streamly/feature/downloads/**
- app/src/main/java/com/example/streamly/core/download/StreamlyDownloadService.kt
- app/src/main/java/com/example/streamly/core/di/{DownloadManagerModule,DatabaseModule,DispatchersModule}.kt
- app/src/main/java/com/example/streamly/core/local/database/StreamlyDatabase.kt
- app/src/main/java/com/example/streamly/core/common/base/dispatcher/Dispatcher.kt
- app/src/main/java/com/example/streamly/core/common/constant/DownloadConstants.kt
- app/src/main/java/com/example/streamly/StreamlyApp.kt
- app/src/main/java/com/example/streamly/StreamlyNavHost.kt
- app/src/main/java/com/example/streamly/feature/player/di/PlayerController.kt
- app/src/main/java/com/example/streamly/feature/player/presentation/{PlayerScreen,PlayerViewModel}.kt
- app/src/main/java/com/example/streamly/feature/player/presentation/component/{ActionButtonsRow,DownloadOptionsBottomSheet}.kt
- app/src/main/java/com/example/streamly/feature/player/presentation/contract/{PlayerUiState,PlayerIntent,PlayerEffect}.kt
- app/src/main/java/com/example/streamly/feature/player/presentation/navigation/PlayerRoute.kt
- app/src/main/java/com/example/streamly/feature/home/presentation/{HomeScreen,HomeViewModel}.kt
- app/src/main/java/com/example/streamly/feature/home/presentation/contract/{HomeIntent,HomeEffect}.kt
- app/src/main/java/com/example/streamly/ui/theme/{Color,Theme}.kt
- app/src/main/AndroidManifest.xml
- app/src/main/res/values/strings.xml
- app/build.gradle.kts
- gradle/libs.versions.toml

--------------------------------------------------------------------------------------------------------------

## 2026-07-28
### Agent
Claude Code

### Commit
766ebc2

### Task
Add an AGENTS.md rule for extracting oversized screen composables into `component/` files with previews.


### Changes
- Added a `component/` rule: when `<Feature>Screen.kt` grows too big, extract each sub-composable into its own file under `presentation/component/`, named after the composable, one composable per file
- Required every `component/` file to ship its own `@Preview`(s) with realistic sample state, wrapped in `StreamlyTheme` (plus `Surface(color = MaterialTheme.colorScheme.background)` when the component has no opaque background of its own)
- Cross-referenced the new rule from the existing `## Previews` section

### Files
- AGENTS.md

--------------------------------------------------------------------------------------------------------------

## 2026-07-28
### Agent
Claude Code

### Commit
9555ab6

### Task
Add the Profile sign-out flow, extract Profile's sub-composables into `component/` with previews, make the header draw full-screen behind the status bar, and wire Home -> Profile navigation.

### Prompt
Implement the provided Profile screen UI. Show the user's avatar, name, email, menu items (Downloads, History, Settings), and a Sign Out action with a confirmation dialog. Confirming sign-out should clear the session and return the user to the onboarding flow. Follow the project's existing architecture and coding conventions.

### Changes
- `ProfileViewModel`: skip the state update when a profile emission has both `name` and `email` null/blank, so clearing the session doesn't flash fallback text before the `NavigateToOnboarding` effect fires
- Renamed `ObserveUserProfileUseCase` → `GetUserProfileUseCase`; added `SignOutUseCase` (clears the session via `AppPreferences.clearSession()`)
- Added the sign-out confirmation flow: `ProfileIntent.SignOutClicked/SignOutConfirmed/SignOutDismissed`, `ProfileUiState.isSignOutDialogVisible`, `ProfileEffect.NavigateToDownloads`/`NavigateToOnboarding`
- Extracted `ProfileHeader`, `ProfileMenuItem`, `SignOutConfirmationDialog` out of `ProfileScreen.kt` into `feature/profile/presentation/component/`, each with its own `@Preview`(s) wrapped in `StreamlyTheme` + `Surface(color = MaterialTheme.colorScheme.background)`
- `ProfileScreen`'s `Scaffold` now uses `contentWindowInsets = WindowInsets.navigationBars` (previously the default, which also reserved status-bar space) so the gradient header bleeds under the status bar; `ProfileHeader`'s back button applies `.windowInsetsPadding(WindowInsets.statusBars)` so it still clears the status bar icons/notch
- Added a Profile entry point icon to Home's `TopAppBar` (`HomeIntent.OnProfileClicked` / `HomeEffect.NavigateToProfile`) and wired `profileEntries(onNavigateToDownloads, onSignedOut)` in `StreamlyNavHost`

### Files
- app/src/main/java/com/example/streamly/feature/profile/**
- app/src/main/java/com/example/streamly/feature/home/presentation/HomeScreen.kt
- app/src/main/java/com/example/streamly/feature/home/presentation/HomeViewModel.kt
- app/src/main/java/com/example/streamly/feature/home/presentation/contract/{HomeIntent,HomeEffect}.kt
- app/src/main/java/com/example/streamly/StreamlyNavHost.kt
- app/src/main/res/values/strings.xml

--------------------------------------------------------------------------------------------------------------

## 2026-07-28
### Agent
Claude Code

### Commit
634b24d

### Task
Refactor the Player screen's layout/insets/transport controls and add app-wide image crossfade.

### Changes
- Reworked the mobile Player layout: `PlayerSurface`, metadata, action buttons, and the "Up Next" header now sit in a fixed (non-scrolling) `Column`; only the up-next video list scrolls, in its own `LazyColumn`
- `PlayerSurface` now paints a solid status-bar-height strip above the video (`windowInsetsTopHeight(WindowInsets.statusBars)`) instead of letting the video draw under a translucent status bar; `PlayerScreenRoute` flips the system status bar icons to light/white for the duration of the screen via `WindowInsetsControllerCompat`, restoring the previous appearance on exit
- Added nav-bar `contentPadding`/`windowInsetsPadding` to the scrollable regions in both the single-pane and two-pane Player layouts so content isn't hidden behind the system navigation bar
- Fixed a real rotation bug: a landscape phone reports `WindowHeightSizeClass.Compact`, and the fixed (non-scrolling) player section could exceed that short viewport with no way to scroll to the rest of the screen. 
- Removed the default previous/next-media buttons from `PlayerView`'s transport controls (`setShowPreviousButton(false)`/`setShowNextButton(false)`) — up next is a separate list, not a queue the player itself skips through
- Unified the forward/back seek increment to 5s (`ExoPlayer.Builder.setSeekForwardIncrementMs`/`setSeekBackIncrementMs`, Media3's default is 15s forward / 5s back); added `AppConstants.SEEK_INCREMENT_MS`
- `StreamlyApp` now implements Coil's `ImageLoaderFactory`, providing one app-wide `ImageLoader` with `crossfade(true)` so thumbnails fade in instead of popping in abruptly during scroll (Home feed, Up Next, Downloads, Shorts)

### Files
- app/src/main/java/com/example/streamly/StreamlyApp.kt
- app/src/main/java/com/example/streamly/core/common/constant/AppConstants.kt
- app/src/main/java/com/example/streamly/feature/player/di/{PlayerController,PlayerModule}.kt
- app/src/main/java/com/example/streamly/feature/player/presentation/PlayerScreen.kt
- app/src/main/java/com/example/streamly/feature/player/presentation/component/{PlayerSurface,UpNextItems}.kt

--------------------------------------------------------------------------------------------------------------

## 2026-07-29
### Agent
Claude Code

### Commit
32b5857

### Task
Fix a cluster of Player screen and system-bar bugs found during on-device testing: rotation state loss, notch/nav-bar insets, a frozen video frame lingering through back-navigation, and status/navigation bar icon contrast across Home, Profile, and Shorts.

### Changes
- `PlayerViewModel`: `start()` is now a true one-shot (`hasStarted` flag) instead of comparing against the stale nav-arg videoId, so rotating after picking an Up Next video no longer reverts playback to the original video
- `PlayerSurface`: video, native transport controls, and back/mute buttons are now inset from the status bar/display cutout/navigation bar in both portrait and fullscreen landscape, instead of drawing under them
- `PlayerViewModel.releaseForExit()` + `PlayerScreenRoute`'s `isPlayerVisible` (`rememberSaveable`) flag: dropping the player reference on back-press removes the video `AndroidView`/`SurfaceView` from composition immediately, instead of a paused/released frame lingering on screen for the whole back-navigation transition
- Added `core/designsystem/component/SystemBarsAppearance.kt`: a single reactive status/navigation-bar icon-color source of truth, driven by the current back-stack destination in `StreamlyNavHost`, replacing independent per-screen mount/dispose overrides that raced each other whenever two screens' Nav3 transition lifecycles overlapped
- `themes.xml`: `Theme.Streamly` now explicitly declares `windowLightStatusBar`, `enforceStatusBarContrast`/`enforceNavigationBarContrast` = false, resolved once at window creation instead of via a runtime `Window` API call
- `MainActivity`: `enableEdgeToEdge()` pinned to `SystemBarStyle.light(...)` for both bars instead of the system-dark-mode-following `auto` default
- `Theme.kt`/`Color.kt`: explicitly pinned `onSurfaceVariant`, `surfaceVariant`, `outlineVariant`, `error`, `onError` to the same values in both color schemes — previously unset, so they silently fell back to Material3's differing light/dark defaults despite the "one fixed scheme" rule, washing out secondary text and chip labels in system dark mode
- `ProfileHeader`: back button and avatar/name content now sit in a single inset-padded inner `Box` (matching `PlayerSurface`'s background-layer/content-layer pattern) instead of padding only the back button individually

### Files
- app/src/main/java/com/example/streamly/MainActivity.kt
- app/src/main/java/com/example/streamly/StreamlyNavHost.kt
- app/src/main/java/com/example/streamly/core/common/extension/ContextExt.kt
- app/src/main/java/com/example/streamly/core/designsystem/component/SystemBarsAppearance.kt
- app/src/main/java/com/example/streamly/feature/player/presentation/{PlayerScreen,PlayerViewModel}.kt
- app/src/main/java/com/example/streamly/feature/player/presentation/component/PlayerSurface.kt
- app/src/main/java/com/example/streamly/feature/profile/presentation/component/ProfileHeader.kt
- app/src/main/java/com/example/streamly/ui/theme/{Color,Theme}.kt
- app/src/main/res/values/themes.xml

--------------------------------------------------------------------------------------------------------------

## 2026-07-29
### Agent
Claude Code

### Commit
019f001

### Task
Fix a cluster of Shorts screen bugs found during on-device testing: notch/nav-bar insets in landscape, the progress bar overlapping the action rail, no loading feedback per item, a stuck-on-pause-lost-on-rotation bug, and permanently-visible transport controls.

### Changes
- `ShortPagerItem`: the whole content layer (video included, not just the overlay) is now inset with `WindowInsets.safeDrawing`, matching the same reasoning as the Player screen fix — Shorts is full-bleed on every edge, so a cutout or the navigation bar can land anywhere depending on rotation
- Fixed the progress bar running under the action rail's mute button in landscape: rather than a fixed end-padding guess, `ShortPagerItem` now branches on `windowSizeClass.heightSizeClass == Compact` — in that mode the progress bar/caption column and the action rail share one `Row` (`Modifier.weight(1f)` on the column) so Compose measures the rail's actual width, instead of two independently-positioned elements that only avoided overlapping by chance of screen proportions
- Added a buffering/loading indicator: `rememberIsBuffering` (`ShortBufferingState.kt`) observes `Player.EVENT_PLAYBACK_STATE_CHANGED` via Media3 Compose's own `Player.observeState(...)` utility (no ready-made "buffering" state holder exists, unlike play/pause/progress); shown via the existing shared `CircularCommonLoader` (`isBlockingInteraction = false` so the action rail stays usable while a single item loads) in place of the transport controls, rather than overlaid on top of them
- Extracted `ShortThumbnail` into its own file, matching the project's one-composable-per-file convention (was inlined in `ShortPagerItem.kt`)
- Rebuilt `ShortProgressBar` as a thin red line with a small dot thumb (matching YouTube Shorts' reference look, per user request) instead of Material3's default thick track/large thumb. First attempt customized `Slider`'s `thumb`/`track` slots, but that API's internal layout math positions the two slots using assumptions tied to the default (much larger) thumb size, leaving the dot visibly off-center against a thin custom track no matter how the slots were resized (confirmed via pixel-level measurement) — replaced with a hand-built `Box` + `detectHorizontalDragGestures`, track and thumb both aligned against the same parent, so there's nothing hidden left to fight
- `ShortsViewModel.onPageChanged()`: added a `hasStartedFirstPage` guard so a call replaying the *same* index (not a genuine swipe) is a no-op. `rememberPagerState()` isn't preserved across configuration changes, so its `snapshotFlow(pagerState.currentPage)` collector re-subscribes fresh after rotation and immediately replays the current page, which `onPageChanged` couldn't previously distinguish from a real swipe — it force-set `playWhenReady = true` every time, silently resuming a video the user had manually paused right before rotating. `currentIndex` defaults to `0`, same as the very first page, so a plain index-equality guard alone would have also blocked the legitimate first autoplay - `hasStartedFirstPage` lets that one call through regardless of index
- `ShortPlayerControls` (play/pause + both seek buttons) now starts hidden and fades in on tap instead of sitting on top of the video permanently; auto-hides after 3 seconds of no further interaction (matching ExoPlayer's own default `PlayerView` timeout) but only while actively playing — pausing leaves controls visible indefinitely, since hiding them would strand the user with no visible way to resume. A passive (non-consuming) `pointerInput` over the controls region resets the hide timer on taps to individual buttons too, not just the background tap that also toggles play/pause

### Files
- app/src/main/java/com/example/streamly/feature/shorts/presentation/ShortsScreen.kt
- app/src/main/java/com/example/streamly/feature/shorts/presentation/ShortsViewModel.kt
- app/src/main/java/com/example/streamly/feature/shorts/presentation/component/ShortPagerItem.kt
- app/src/main/java/com/example/streamly/feature/shorts/presentation/component/ShortProgressBar.kt
- app/src/main/java/com/example/streamly/feature/shorts/presentation/component/ShortBufferingState.kt
- app/src/main/java/com/example/streamly/feature/shorts/presentation/component/ShortThumbnail.kt

--------------------------------------------------------------------------------------------------------------

## 2026-07-29
### Agent
Claude Code

### Commit
10fa6a1

### Task
Add real download progress/completion notifications, a notification permission request, and fix downloads getting stuck after a force-stop.


### Changes
- `StreamlyDownloadService.getForegroundNotification()` now builds a real progress notification via Media3's `DownloadNotificationHelper` (title + percentage progress bar, refreshed on Media3's own update interval) instead of a bare "ongoing" placeholder notification
- Added `DownloadNotifier` (`feature/downloads/data/download/`) — a `DownloadManager.Listener` (the same integration point `Media3DownloadListener` already uses to mirror state to Room) that posts a one-shot completed/failed notification per video, since `DownloadService` itself exposes no per-download callback; wired up alongside `Media3DownloadListener` in `StreamlyApp.onCreate()`
- Added the `POST_NOTIFICATIONS` manifest permission and a runtime request on the Authentication screen (`RequestNotificationPermissionOnce` in `AuthenticationScreen.kt`).
- Fixed downloads staying permanently stuck after the app is force-stopped mid-download: `StreamlyApp.onCreate()` now also calls `DownloadService.start(this, StreamlyDownloadService::class.java)` (the standard Media3 pattern), so any `QUEUED`/`DOWNLOADING` download persisted in the index actually resumes on next launch instead of sitting there indefinitely

### Files
- app/src/main/java/com/example/streamly/core/service/StreamlyDownloadService.kt
- app/src/main/java/com/example/streamly/feature/downloads/data/download/DownloadNotifier.kt
- app/src/main/java/com/example/streamly/feature/auth/authentication/presentation/AuthenticationScreen.kt
- app/src/main/java/com/example/streamly/StreamlyApp.kt
- app/src/main/AndroidManifest.xml
- app/src/main/res/values/strings.xml

--------------------------------------------------------------------------------------------------------------

## 2026-07-29
### Agent
Claude Code

### Commit
36e6944

### Task
Fix :  Player leaving the user stranded on a deleted video after removing the currently playing completed download.

### Changes
- `PlayerViewModel.removeCurrentDownload()`: when the deleted download was `COMPLETED` (i.e. actively backing the video being watched), releases the player and sends a new `PlayerEffect.NavigateToDownloads` instead of leaving Player on-screen with no video
- `PlayerScreenRoute` handles `PlayerEffect.NavigateToDownloads` by hiding the player and invoking a new `onNavigateToDownloadsAfterDelete` callback (plumbed through `playerEntries()`/`PlayerRoute.kt`)
- `StreamlyNavHost` wires `onNavigateToDownloadsAfterDelete` to strip every `PlayerNavKey` entry from the back stack (not just pop one) before landing on Downloads — Player can be pushed from Downloads directly or from its own "download started" snackbar action, so a plain back-pop could otherwise resurrect a stale entry for the now-deleted video
- `DownloadsScreen`: replaced the private top-level `formatBytes()` function with a `Long.formatAsGigabytes()` extension

### Files
- app/src/main/java/com/example/streamly/StreamlyNavHost.kt
- app/src/main/java/com/example/streamly/feature/downloads/presentation/DownloadsScreen.kt
- app/src/main/java/com/example/streamly/feature/player/presentation/PlayerScreen.kt
- app/src/main/java/com/example/streamly/feature/player/presentation/PlayerViewModel.kt
- app/src/main/java/com/example/streamly/feature/player/presentation/contract/PlayerEffect.kt
- app/src/main/java/com/example/streamly/feature/player/presentation/navigation/PlayerRoute.kt

--------------------------------------------------------------------------------------------------------------

## 2026-07-29
### Agent
Claude Code

### Commit
b960ea2

### Task
Track how a user signed in (guest / email / Google) and block guests from downloading videos in Player.

### Prompt
"first you have to check is that continue as guest. then you have to save as a userType Guest. After that when video playing and click the download button below playerview, if this is guest then said 'Please sign in with your email or continue with Google to download videos.' In Guest Mode Profile Section will be name Guest, No email added — you already handle this. If login with email then you already get the name and email. If login with Google then display name as Google User, email google.user@example.com. You don't make any work on profile screen."

### Changes
- Added `core/common/enum/UserType.kt` (`GUEST`/`EMAIL`/`GOOGLE`); extended `AppPreferences`/`AppPreferencesImpl` with `userTypeFlow`/`setUserType()` backed by a new `KEY_USER_TYPE` DataStore key
- `SignInUseCase` (Google/guest path) now takes a `UserType`: persists it always, and for `GOOGLE` also saves a fixed placeholder identity (`AppConstants.GOOGLE_USER_NAME` = "Google User", `AppConstants.GOOGLE_USER_EMAIL` = "google.user@example.com") since there's no real Google Sign-In integration yet
- `AuthenticationViewModel` passes `UserType.GOOGLE`/`UserType.GUEST` to `SignInUseCase` from the respective button intents
- Renamed `SaveUserProfileUseCase` → `SignInWithEmailUseCase`; it now also persists `UserType.EMAIL` alongside the captured name/email
- Added `IsGuestUserUseCase` (`feature/player/domain/usecase/`), injected into `PlayerViewModel`; `onDownloadIconClicked()` checks it before starting a download and sends a new `PlayerEffect.ShowGuestDownloadBlockedSnackbar` (wired in `PlayerScreen.kt` to a snackbar with the new `player_guest_download_blocked_message` string) instead, for guests only
- Profile screen left untouched — its existing null-name/null-email fallback to "Guest" / "No email added" already covers the guest case correctly

### Files
- app/src/main/java/com/example/streamly/core/common/constant/{AppConstants,DataStoreConstants}.kt
- app/src/main/java/com/example/streamly/core/common/enum/UserType.kt
- app/src/main/java/com/example/streamly/core/data/storage/datastore/AppPreferencesImpl.kt
- app/src/main/java/com/example/streamly/core/domain/storage/datastore/AppPreferences.kt
- app/src/main/java/com/example/streamly/feature/auth/authentication/domain/usecase/SignInUseCase.kt
- app/src/main/java/com/example/streamly/feature/auth/authentication/presentation/AuthenticationViewModel.kt
- app/src/main/java/com/example/streamly/feature/auth/signinwithemail/domain/usecase/SignInWithEmailUseCase.kt
- app/src/main/java/com/example/streamly/feature/auth/signinwithemail/presentation/SignInWithEmailViewModel.kt
- app/src/main/java/com/example/streamly/feature/player/domain/usecase/IsGuestUserUseCase.kt
- app/src/main/java/com/example/streamly/feature/player/presentation/PlayerScreen.kt
- app/src/main/java/com/example/streamly/feature/player/presentation/PlayerViewModel.kt
- app/src/main/java/com/example/streamly/feature/player/presentation/contract/PlayerEffect.kt
- app/src/main/res/values/strings.xml

--------------------------------------------------------------------------------------------------------------

## 2026-07-29
### Agent
Claude Code

### Commit
b080189

### Task
Add app-wide no-internet detection with a global snackbar, and auto-recover playback that stalls due to a network load error, in both Shorts and the normal Player.

### Prompt
Implement a centralized No Internet feature. Whenever the device is offline, display a Snackbar on the current screen.

### Changes
- Added `core/domain/connectivity/NetworkMonitor.kt` (interface) + `core/data/connectivity/NetworkMonitorImpl.kt` (`ConnectivityManager.NetworkCallback`-backed `callbackFlow`, tracking the full set of matching networks rather than trusting a single "active" one, `flowOn(IO)`, `.conflate()`), bound via `core/di/ConnectivityModule.kt`; added the `ACCESS_NETWORK_STATE` manifest permission
- Added `MainScreen.kt` + `MainViewModel.kt` (app root, alongside `MainActivity`): `MainScreen` wraps `StreamlyNavHost()` (kept navigation-only) in a `Box` with its own `SnackbarHost`, showing an indefinite-duration "No internet connection" snackbar for as long as `MainViewModel.isOffline` is true — dismissed automatically when `LaunchedEffect(isOffline)`'s key change cancels the in-flight `showSnackbar()` call on reconnect
- Diagnosed why playback doesn't recover on its own: ExoPlayer's default `LoadErrorHandlingPolicy` retries a failed load ~6 times with backoff, then gives up, fires `onPlayerError`, and parks the player in terminal `STATE_IDLE` — it does not retry again once connectivity returns, and (contrary to first assumption) a bare `prepare()` alone doesn't resume playback either; `playWhenReady` has to be re-asserted too, matching what tapping the native play/pause button does under the hood
- Added `core/domain/connectivity/ObserveNetworkReconnectedUseCase.kt` — emits once per offline→online transition (`distinctUntilChanged().drop(1).filter{it}`); shared by both features (moved here from two near-identical per-feature copies after being asked not to duplicate it)
- `ShortsPlayerPool.retryErroredPlayers(currentIndex)` (new) — re-`prepare()`s any pooled player with a non-null `playerError`, forcing `playWhenReady` only for `currentIndex` so a failed pre-buffer on the adjacent (not-yet-visible) short doesn't auto-start it; `ShortsViewModel` collects the reconnect use case in `init` and calls it
- `PlayerController.retryIfErrored()` (new) — same fix for the single-video Player; `PlayerViewModel` collects the same use case in `init`
- `ShortBufferingState.rememberIsBuffering()`: was only true for `STATE_BUFFERING`, so the spinner vanished the instant ExoPlayer gave up retrying, leaving a silently frozen frame for the whole outage (only reappearing once reconnect-retry re-entered `STATE_BUFFERING`); now also true for `STATE_IDLE` with a non-null `playerError`, and observes `Player.EVENT_PLAYER_ERROR` alongside the existing playback-state event
- Fixed a dark-mode theming bug found along the way: `ModalBottomSheet`/`AlertDialog` default to `surfaceContainerLow`/`surfaceContainerHigh`, roles `Theme.kt` doesn't pin identically across light/dark (only `surface`/`onSurface` and a few others are), so they silently fell back to Material3's differing dark-mode defaults and rendered near-black-on-black; `DownloadOptionsBottomSheet` and `SignOutConfirmationDialog` now pin `containerColor`/content colors explicitly to `MaterialTheme.colorScheme.surface`/`onSurface`
- Added `AppConstants.CONNECTIVITY_STOP_TIMEOUT_MILLIS` and `AppConstants.SHORTS_PLAYER_POOL_MAX_SIZE`; `ShortsPlayerPool`'s seek-increment constant now reuses the existing `AppConstants.SEEK_INCREMENT_MS` instead of duplicating it

### Files
- app/src/main/java/com/example/streamly/MainActivity.kt
- app/src/main/java/com/example/streamly/MainScreen.kt
- app/src/main/java/com/example/streamly/MainViewModel.kt
- app/src/main/java/com/example/streamly/core/common/constant/AppConstants.kt
- app/src/main/java/com/example/streamly/core/data/connectivity/NetworkMonitorImpl.kt
- app/src/main/java/com/example/streamly/core/di/ConnectivityModule.kt
- app/src/main/java/com/example/streamly/core/domain/connectivity/{NetworkMonitor,ObserveNetworkReconnectedUseCase}.kt
- app/src/main/java/com/example/streamly/feature/player/di/PlayerController.kt
- app/src/main/java/com/example/streamly/feature/player/presentation/PlayerViewModel.kt
- app/src/main/java/com/example/streamly/feature/player/presentation/component/DownloadOptionsBottomSheet.kt
- app/src/main/java/com/example/streamly/feature/profile/presentation/component/SignOutConfirmationDialog.kt
- app/src/main/java/com/example/streamly/feature/shorts/di/ShortsPlayerPool.kt
- app/src/main/java/com/example/streamly/feature/shorts/presentation/ShortsViewModel.kt
- app/src/main/java/com/example/streamly/feature/shorts/presentation/component/ShortBufferingState.kt
- app/src/main/AndroidManifest.xml
- app/src/main/res/values/strings.xml

--------------------------------------------------------------------------------------------------------------

## 2026-07-29
### Agent
Claude Code

### Commit
d72e3f0

### Task
Prepare README.md to match the current state of the codebase and cover the assignment brief's required deliverables (setup, architecture decisions, AI-assisted workflow, shortcuts taken and why); move DAO/Entity out of the documented `core/local/` structure in AGENTS.md.

### Changes
- Rewrote README.md: accurate `Screens` table mapping all 7 required screens to their location and functional status, refreshed `Tech Stack`/`Project Structure` tree (previously only listed `home`/`profile`; now covers `auth` (authentication + signinwithemail), `home`, `shorts`, `player`, `downloads`, `profile`, `splash`, plus `MainScreen`/`MainViewModel`/connectivity additions), `Playback (Media3)` section, `Networking & Data` (mocked Ktor client disclosure), `AI-Assisted Development` section (Claude Code, AGENTS.md symlink strategy incl. a macOS-vs-Windows `core.symlinks` caveat, commit co-authorship, `docs/AGENTS_CHANGELOG.md` trail), `Shortcuts & Known Gaps` (Google Sign-In stub, mocked video data, static category chips, non-functional Shorts/Player stubs, disabled Profile menu items, limited device coverage — emulator + one real phone only), corrected `Getting Started` clone URL/directory
- Replaced the plain-text `Presentation ↓ Domain ↓ Data` arrow diagram with a verified-aligned ASCII box diagram (after a Mermaid attempt didn't render well and a manually-retyped box diagram broke alignment — regenerated and spliced in programmatically to guarantee correct character-width alignment)
- `AGENTS.md`: removed `dao/`and `entity/` from the `core/local/` tree and its `## local/` prose section; `Contains` is now just Database/Migrations, and a new rule states DAO/Entity are feature-specific and belong in `feature/<feature>/data/datasource/local/`, matching how `DownloadDao`/`DownloadEntity` are already actually organized

### Files
- README.md
- AGENTS.md

--------------------------------------------------------------------------------------------------------------

## 2026-07-29
### Agent
Claude Code

### Commit
d0cf1f4

### Task
Polish the Splash and Authentication screens, and fix a real dark-icon system-bar bug on both.


### Changes
- `SplashScreen`: replaced the bare centered spinner with the app name (`stringResource(R.string.app_name)`, "Streamly") and a `CircularProgressIndicator` grouped directly beneath it in one centered `Column` (not pinned separately to the bottom)
- `AuthenticationScreen`: the previously-empty 72dp logo placeholder now shows "S" (new `R.string.app_name_initial`); content now sits in a `.verticalScroll(rememberScrollState())` `Column` sized to `.fillMaxSize()` so it centers by default and only scrolls once it overflows (e.g. short-height landscape) — the earlier version's scroll wasn't actually height-bounded, so it did nothing useful; also added `.windowInsetsPadding(WindowInsets.safeDrawing)` so content clears the status bar/notch/nav bar in any orientation while the gradient background itself stays a true edge-to-edge `fillMaxSize()`
- Found and fixed a real bug in `StreamlyNavHost.kt`: `AuthenticationNavKey`/`SplashNavKey` weren't excluded from the "light" (dark-icon) branches of `SystemBarsAppearance`, so both screens got dark status/nav-bar icons despite having the same dark blue gradient background as Player/Shorts — rendering them near-invisible. Added both to the light/white-icon branches, matching how Player/Shorts already handle it
- `AuthenticationScreen` didn't use `WindowSizeClass` at all (a hardcoded `widthIn(max = 480.dp)` applied unconditionally) despite AGENTS.md requiring layout decisions to derive from it — added `windowSizeClass: WindowSizeClass` parameter end to end (`AuthenticationScreenRoute` → `LocalWindowSizeClass.current`) and gated the width cap behind `isWideLayout`, matching `ProfileScreen`'s exact existing pattern; previews now construct explicit `WindowSizeClass.calculateFromSize(...)` per device instead of not testing size-class branching at all
- `AppConstants.SPLASH_MIN_DISPLAY_DURATION` bumped 800ms → 1000ms

### Files
- app/src/main/java/com/example/streamly/StreamlyNavHost.kt
- app/src/main/java/com/example/streamly/core/common/constant/AppConstants.kt
- app/src/main/java/com/example/streamly/feature/auth/authentication/presentation/AuthenticationScreen.kt
- app/src/main/java/com/example/streamly/feature/splash/presentation/SplashScreen.kt
- app/src/main/res/values/strings.xml

--------------------------------------------------------------------------------------------------------------

## 2026-07-29
### Agent
Claude Code

### Commit
8b2723b

### Task
Adopt the AndroidX SplashScreen API to remove the white flash before the app's cold-start window draws its first frame.


### Changes
- Added `androidx.core:core-splashscreen` via the version catalog (`gradle/libs.versions.toml` + `app/build.gradle.kts`)
- Added `Theme.Streamly.Splash` (`themes.xml`, extends `Theme.SplashScreen`) with `windowSplashScreenBackground` set to a new `@color/splash_background` (matches `StreamlyBlueContainer`, the top color of `SplashScreen`'s gradient, so the handoff into the Compose splash has no color jump), `windowSplashScreenAnimatedIcon` pointing at the existing (default/unmodified) `ic_launcher_foreground`, and `postSplashScreenTheme` back to `Theme.Streamly`
- `AndroidManifest.xml`: the launcher `<activity>` now uses `Theme.Streamly.Splash` instead of `Theme.Streamly` directly
- `MainActivity.kt`: calls `installSplashScreen()` immediately before `super.onCreate()`, which performs the theme handoff at the right moment

### Files
- app/build.gradle.kts
- gradle/libs.versions.toml
- app/src/main/AndroidManifest.xml
- app/src/main/java/com/example/streamly/MainActivity.kt
- app/src/main/res/values/colors.xml
- app/src/main/res/values/themes.xml
