# Streamly

Streamly is a video streaming Android app built for a home assignment, focused on Clean
Architecture, an offline-first mindset, and production-quality Kotlin/Compose code.

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose (no XML layouts, no Fragments)
- **Architecture**: Clean Architecture (Presentation → Domain → Data) + MVVM/MVI
- **Async**: Kotlin Coroutines + Flow
- **DI**: Hilt
- **Navigation**: Navigation 3
- **Networking**: Ktor
- **Local persistence**: Room, DataStore
- **Media playback**: Media3 (ExoPlayer)
- **Build**: Gradle Kotlin DSL, version catalog (`gradle/libs.versions.toml`)

## Architecture

```
Presentation
    ↓
Domain
    ↓
Data
```

- Presentation never talks to Data directly.
- Repository *interfaces* live in Domain; *implementations* live in Data.
- Every screen follows MVI: `UiState`, `UiIntent`, `UiEffect`, and a `ViewModel` extending the
  shared `core/common/base/MVIViewModel<S, I, E>`.
- The Domain layer is pure Kotlin — no Android framework, no Compose, no Room/Ktor types leak
  in.

See [AGENTS.md](AGENTS.md) for the full set of architectural rules this codebase follows —
it's the single source of truth for both human and AI contributors (`CLAUDE.md`,
`.cursor/rules`, `.codex/instructions.md`, and `.antigravity/rules` are symlinks to it).

## Project Structure

```
app/src/main/java/com/example/streamly/
├── core/
│   ├── common/        # base classes, extensions, utils, constants, enums
│   ├── domain/        # shared contracts (e.g. storage interfaces)
│   ├── data/           # shared contract implementations
│   ├── network/       # Ktor HttpClient wrapper, Json config
│   ├── local/         # Room database, DAO, entities
│   ├── navigation/     # Navigation 3 host wiring, shared nav types
│   └── di/             # Hilt modules for shared infrastructure
│
├── feature/
│   ├── home/
│   └── profile/
│       └── each feature: data/ domain/ presentation/ di/
│
├── StreamlyApp.kt          # @HiltAndroidApp
├── MainActivity.kt
└── StreamlyNavHost.kt       # aggregates every feature's navigation entries
```

Every feature owns its own `presentation/navigation/<Feature>Route.kt` (its `NavKey` + entry
registration); `StreamlyNavHost.kt` is the one place that stitches every feature into a single
back stack.

## Requirements

- Android Studio (recent stable), JDK 11+
- `compileSdk`/`targetSdk` 36, `minSdk` 24
- Gradle 9.5 (via the wrapper — no local Gradle install needed)

## Getting Started

```bash
git clone <repo-url>
cd Assignment
./gradlew assembleDebug
```

Or open the project in Android Studio and run the `app` configuration on an emulator/device.

## Changelog

AI-agent-assisted work on this codebase is logged chronologically in
[docs/AGENTS_CHANGELOG.md](docs/AGENTS_CHANGELOG.md).