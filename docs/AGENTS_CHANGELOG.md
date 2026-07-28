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
Follow-up requests on `feature/profile`: "handle null here" (guard the profile collector against a blank/null emission right after sign-out); "cut this type of class and put into the component inside feature/presentation" for `ProfileMenuItem`, then `ProfileHeader`/`SignOutConfirmationDialog`; "remove status bar i need pure full screen"; "add preview for prfoile menuteim and sigout dilaog" / "profileheader also" / "set background for preview looks ugly".

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
