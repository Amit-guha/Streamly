# Streamly

Streamly is a minimal video streaming Android app built with Kotlin, Jetpack Compose, and Media3 (ExoPlayer). It features two playback surfaces (normal videos and vertical shorts), offline downloads, and clean architecture with MVVM + MVI pattern.


## AI-Assisted Development

This project was built with [Claude Code](https://claude.com/claude-code) as the agentic coding
tool throughout, not just autocomplete.

- **One rule set, symlinked everywhere**: [AGENTS.md](AGENTS.md) is the single source of truth;
  `CLAUDE.md`, `.cursor/rules`, `.codex/instructions.md`, and `.antigravity/rules` are symlinks to
  it, so every tool reads the same rules and none of them can drift into a separate copy.
- **macOS vs. Windows**: these were created and committed as real symlinks on macOS. Git on
  Windows only checks them out as actual symlinks if `core.symlinks` is enabled *and* the account
  has symlink privileges (Developer Mode on Windows 10/11, or an elevated/Administrator shell) —
  otherwise each file checks out as a small text file containing the literal target path instead
  of its contents. If that happens: run `git config core.symlinks true` and re-clone (or
  `git checkout -- .` after enabling it), or just work from [AGENTS.md](AGENTS.md) directly — it's
  the same content either way, since it's the actual source file, not a symlink itself.
- **Workflow**: features were built iteratively, one at a time, against the architecture rules in
  AGENTS.md — auth → Home → Player → Shorts → Downloads → Profile/sign-out — with each
  change verified via `./gradlew :app:compileDebugKotlin` before moving on, plus real on-device
  testing feedback loops — several genuine bugs were found and fixed this way, not pre-scripted.
- **Commit history reflects the agent in the loop**: every AI-assisted commit carries a
  `Co-Authored-By: Claude` trailer.
- **Prompt/changelog trail**: [docs/AGENTS_CHANGELOG.md](docs/AGENTS_CHANGELOG.md) logs every
  agent-assisted commit — the task, the prompt that drove it, what changed, and which files —
  added at the time the work happened.

## Screens

| # | Screen | Where it lives | Notes                                                                                                                                                                        |
|---|--------|-----------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 1 | Onboarding | `feature/auth/authentication`, `feature/auth/signinwithemail` | "Continue with Google" (mocked), "Sign in with email" (name & email capture), "Continue as guest" — session persisted via DataStore, authenticated users go straight to Home |
| 2 | Home feed | `feature/home` | Scrollable video cards (thumbnail/title/channel/views/age); adapts to a single column on phones and a `LazyVerticalGrid` on foldables/tablets                                |
| 3 | Shorts | `feature/shorts` | Full-screen vertical pager, autoplay on the visible item, swipe to advance, pooled ExoPlayer strategy (max 2 alive), automatic retry/resume if a video gets stuck from a lost network connection and comes back, handles rotation/config-change and pause-on-background/resume-on-return lifecycle |
| 4 | Player (normal video) | `feature/player` | 16:9 player, standard controls, metadata, download action, "up next" list, automatic retry/resume if a video gets stuck from a lost network connection and comes back, handles rotation/config-change and pause-on-background/resume-on-return lifecycle |
| 5 | Downloads | `feature/downloads` | Real Media3 `DownloadManager`-backed progress (not simulated), completed items play from local storage, remove action, notification progress bar, resumes automatically once a lost network connection is restored |
| 6 | Profile | `feature/profile` | Avatar, name/email (real if signed in with email, a mocked "Guest" placeholder otherwise); of the menu links only Downloads and Sign out are functional — Watch history and Settings are disabled stubs |
| 7 | Sign-out | `feature/profile/presentation/component/SignOutConfirmationDialog.kt` | Confirmation dialog; confirming clears the session and returns to onboarding                                                                                                 |

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose (no XML layouts, no Fragments)
- **Architecture**: Clean Architecture (Presentation → Domain → Data) + MVVM/MVI
- **Async**: Kotlin Coroutines + Flow (no RxJava)
- **DI**: Hilt
- **Navigation**: Navigation 3
- **Networking**: Ktor (mocked engine backed by local JSON assets — see [Networking & Data](#networking--data))
- **Local persistence**: Room (downloads index), DataStore (session/preferences)
- **Media playback**: Media3 (ExoPlayer), HLS only via `media3-exoplayer-hls`
- **Images**: Coil, one app-wide `ImageLoader` with crossfade
- **Build**: Gradle Kotlin DSL, version catalog (`gradle/libs.versions.toml`) — every dependency,
  plugin, and version is declared there, never hardcoded in a `build.gradle.kts`

## Architecture(Clean Architecture + MVVM+MVI)

```
┌──────────────────────────────────────────────────┐
│                   Presentation                   │
│  Compose UI · ViewModel · UiState/Intent/Effect  │
└─────────────────────────┬────────────────────────┘
                          │
┌─────────────────────────▼────────────────────────┐
│                      Domain                      │
│    UseCases · Repository interfaces · Models     │
└─────────────────────────┬────────────────────────┘
                          │
┌─────────────────────────▼────────────────────────┐
│                       Data                       │
│   Repository impls · Remote/Local data sources   │
└──────────────────────────────────────────────────┘
```

- Presentation never talks to Data directly; ViewModels talk only to UseCases, never Repositories.
- Repository *interfaces* live in Domain; *implementations* live in Data.
- Every screen follows MVI: `UiState`, `UiIntent`, `UiEffect`, and a `ViewModel` extending the
  shared `core/common/base/MVIViewModel<S, I, E>` (state exposed as read-only `StateFlow`,
  one-time events as `SharedFlow` via `sendEffect()`).
- The Domain layer is pure Kotlin — no Android framework, no Compose, no Room/Ktor types leak in.
- Adaptive layouts are built with `WindowSizeClass` from the start — every screen has phone/
  foldable/tablet previews, not a single fixed-width assumption.

See [AGENTS.md](AGENTS.md) for the full set of architectural rules this codebase follows — it's
the single source of truth for both human and AI contributors (`CLAUDE.md`, `.cursor/rules`,
`.codex/instructions.md`, and `.antigravity/rules` are symlinks to it, so there's one rule set to
maintain, not four drifting copies).

## Project Structure

```
app/src/main/java/com/example/streamly/
├── core/
│   ├── common/          # base/ (MVIViewModel), extension/, util/, constant/, enum/
│   ├── domain/          # shared contracts: storage/datastore, connectivity
│   ├── data/             # shared contract implementations
│   ├── network/         # Ktor HttpClient wrapper, Json config
│   ├── local/            # Room database, migrations
│   ├── designsystem/     # reusable Compose components shared across features
│   ├── navigation/       # Navigation 3 host wiring, shared nav types
│   ├── service/          # shared Android Service entry points (Media3 DownloadService)
│   └── di/               # Hilt modules for shared infrastructure
│
├── feature/                  # every feature below follows: data/ domain/ presentation/ di/
│   ├── auth/
│   │   ├── authentication/    # onboarding: Google / email / guest choice
│   │   └── signinwithemail/   # name + email capture
│   ├── home/                  # long-form video feed
│   ├── shorts/                # vertical Shorts pager
│   ├── player/                # normal-video player
│   ├── downloads/             # offline downloads
│   ├── profile/               # profile + sign-out
│   └── splash/                # session-based start-destination routing
│
├── StreamlyApp.kt          # @HiltAndroidApp
├── MainActivity.kt
├── MainScreen.kt            # app root: hosts StreamlyNavHost + the app-wide offline snackbar
├── MainViewModel.kt         # backs the offline snackbar (connectivity state)
└── StreamlyNavHost.kt       # navigation only — aggregates every feature's nav entries
```


## Playback (Media3)

- **Normal Player**: a single shared `ExoPlayer` instance, created once and reused across
  up-next switches, config changes, and navigation; released only on final screen exit.
  Lifecycle-correct pause on backgrounding / resume on return.
- **Shorts**: a pooled (`ShortsPlayerPool`) capped at 2 live `ExoPlayer`s at once —
  the visible item plus one adjacent item pre-buffering forward — never one player per feed item.
- **Downloads**: Media3's `DownloadManager`/offline module, real progress (polled + listener-driven,
  not simulated), a foreground-service progress notification, and completed/failed one-shot
  notifications. Completed downloads play back from local storage automatically (the player reads
  through the same `CacheDataSource` the download pipeline writes to — no special-casing).
- **Offline resilience**: a shared `NetworkMonitor` (`ConnectivityManager` callback, exposed as a
  `Flow<Boolean>`) drives an app-wide "No internet connection" snackbar. When a video/short's HLS
  load fails outright and ExoPlayer exhausts its own retry/backoff (landing in a terminal
  `STATE_IDLE` + `playerError`), both the Player and Shorts pool automatically retry and resume
  playback the moment connectivity is restored.
- **Delivery format**: HLS only (`media3-exoplayer-hls`, `.m3u8` streams).
## Networking & Data

Home and Shorts go through a genuine Ktor `HttpClient` pipeline (content
negotiation, kotlinx.serialization) backed by a mock engine that serves local JSON assets
(`assets/videos.json`, `assets/shorts.json`) instead of a live endpoint, so swapping in a real
backend later is a data-source change, not an architecture change.


## Shortcuts & Known Gaps

- **Google Sign-In is a stub**, not a real Credential Manager/Google Identity integration.Tapping it persists a fixed placeholder identity
  ("Google User" / `google.user@example.com`) and marks the session signed in, same as the guest
  path but with a `UserType` recorded so Player can tell guests apart from signed-in users (guests
  are blocked from downloading, with a snackbar prompting sign-in). Profile itself doesn't read
  `UserType` — it falls back to "Guest"/"No email added" purely from null name/email.
- **Home/Shorts video data is mocked** (local JSON assets through a real Ktor pipeline, not a live
  API).
- **Home's category chips are static** .
- **Shorts' like/comment/share and Player's like are non-functional stubs** (local UI toggle only,
  not persisted).Player's Share action is real (fires an Android share `Intent`).
- **Profile's "Watch history" and "Settings" menu items are disabled stubs** — no watch-history
  tracking or settings screen exists yet; Profile itself is otherwise fully functional (sign-out, downloads list, avatar/name/email).
- **Limited device coverage** — verified on an emulator and one real Android phone, not across
  the full range of screen sizes/OS versions.

## Requirements

- Android Studio (recent stable), JDK 11+
- `compileSdk`/`targetSdk` 36, `minSdk` 24
- Gradle 9.5 (via the wrapper — no local Gradle install needed)

## Getting Started

```bash
git clone https://github.com/Amit-guha/Streamly.git
cd Streamly
./gradlew assembleDebug
```

Or open the project in Android Studio and run the `app` configuration on an emulator/device.

**Debug APK**: 
