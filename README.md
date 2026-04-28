# MovieBox Android

![CI](https://github.com/satapornsup/moviebox-android/actions/workflows/android.yml/badge.svg)

A small but production-shaped movies app. Scrollable popular list with paginated
load-more, debounced search, detail screen, and favourites — backed by a
[Node + Express + Prisma + MongoDB API](../moviebox-backend) the same author
also wrote.

> **Why it exists:** built as a portfolio piece to demonstrate Clean
> Architecture + Jetpack Compose + Coroutines/Flow + Hilt on a real client/server
> split, deployed end-to-end.

## Screenshots

<p align="center">
  <img src="https://github.com/user-attachments/assets/5604fba8-25fa-49c0-8648-7712fde5f6d4" alt="Home — paginated popular list" width="22%" />
  <img src="https://github.com/user-attachments/assets/c7ee96be-fca8-4b2e-b518-d9133e5e6cf8" alt="Search — debounced 300ms" width="22%" />
  <img src="https://github.com/user-attachments/assets/0bfc7930-c83f-46a8-bd4f-f74d2ab91513" alt="Detail — favourite toggle" width="22%" />
  <img src="https://github.com/user-attachments/assets/8649d067-9069-4b39-9356-b41bb356e5f1" alt="Favourites" width="22%" />
</p>

## Stack

- **Kotlin 2.2** + **Jetpack Compose** (Material 3, BOM 2026.02)
- **MVVM**: `StateFlow` UI state, `sealed interface` UI events, stateless screens
- **Hilt** for DI
- **Retrofit 3** + **Moshi** + **OkHttp** logging interceptor
- **Coil** for poster loading
- **Navigation-Compose** with type-safe int args
- **JUnit4** + **MockK** + **kotlinx-coroutines-test** + **Turbine** for tests
- **GitHub Actions** CI: lint → unit tests → assemble debug APK (uploaded as artifact)

## Architecture

```
┌──────────────── presentation/ ────────────────┐
│                                                │
│   *Route.kt  ─ stateful, observes ViewModel    │
│      │                                         │
│      └── *Screen.kt ─ stateless Composables    │
│                                                │
│   *ViewModel.kt ─ StateFlow<UiState>,          │
│      │              sealed UiEvent             │
│      ▼                                         │
└──────│─────────────────────────────────────────┘
       │
┌──────▼──────── data/repo ─────────────────────┐
│   MovieRepository / FavoriteRepository        │
│      │                                         │
│      ▼                                         │
│   data/remote (Retrofit MovieApi + DTOs)       │
└──────│─────────────────────────────────────────┘
       │
       ▼
   moviebox-backend  (Express + Prisma + Mongo)
```

`domain/model` holds plain Kotlin data classes (`Movie`, `MoviePage`); DTOs in
`data/remote/dto` map to them via `toDomain()` extensions, so the UI never sees
TMDb's `snake_case` shape.

## Project layout

```
app/src/main/java/com/mastercoding/moviebox/
├── MainActivity.kt
├── MovieBoxApp.kt              ── @HiltAndroidApp
├── core/ui/                    ── shared UI bits (ErrorView)
├── data/
│   ├── remote/                 ── Retrofit MovieApi + DTOs
│   └── repo/                   ── MovieRepository, FavoriteRepository
├── di/NetworkModule.kt         ── Hilt @Module providing OkHttp + Retrofit
├── domain/model/               ── pure Kotlin domain types
├── presentation/
│   ├── home/                   ── popular list, search, pagination
│   ├── detail/                 ── movie detail + favourite toggle
│   ├── favorites/              ── user's favourites list
│   └── nav/                    ── NavGraph + Route sealed class
└── ui/theme/                   ── Material 3 theme
```

## Setup

```bash
# 1. Run the backend (or point BASE_URL at a deployed instance)
cd ../moviebox-backend
npm install && npm run prisma:push && npm run seed && npm run dev   # :3000

# 2. Open the Android project
cd ../moviebox-android
# Open in Android Studio → Run on emulator
```

`BASE_URL` is wired through `BuildConfig`:

- **debug** → `http://10.0.2.2:3000/api/` (emulator loopback to host's
  `localhost:3000`, for use with `npm run dev`)
- **release** → `https://moviebox-backend-g4sn.onrender.com/api/`
  (deployed Render instance)

> The Render free tier sleeps after 15 min idle; the first request after
> wake takes ~30s to spin up. OkHttp connect/read timeouts are bumped to
> 30s/60s so the app survives the cold start instead of throwing.

User identity is stubbed via an `x-user-id: demo-user` header injected by an
OkHttp interceptor — kept simple on purpose so the app can ship without
JWT/OAuth scaffolding.

## Key design decisions

A few things worth pointing out for someone reading the source:

**Stateful Route → Stateless Screen split.** Each feature has `*Route.kt`
(stateful, observes ViewModel + injects callbacks) wrapping a `*Screen.kt`
(pure Composable, easy to preview & test). Wiring lives in the Route layer,
visuals in the Screen layer.

**Single-job pattern in `HomeViewModel`.** `loadPopular` and `doSearch` share
one `contentJob: Job?`. Launching a new content operation cancels the in-flight
one, so a stale search coroutine can't race a new popular reload and leave
`_state` inconsistent. `loadMore` stays on its own launch since it's
append-only and has its own guards.

**Debounced search.** `onQueryChange` fires immediately into state (so the
text field updates), then `delay(300)` before hitting the network. Cancelling
the previous content job means only the latest keystroke wins.

**LaunchedEffect key set in `MovieList`.** Pagination almost dead-locked from
a Compose subtlety: when search → clear, `endReached` flips back to `false`
which re-fires the effect, but `loadMore` blocks on `loading=true` and
returns. Once `loadMore` finishes the keys (`shouldLoadMore`, `endReached`,
`isLoadingMore`) hadn't changed, so the effect never re-fires. Fix:
`movies.size` is also in the key set, so list growth re-runs the effect.
([HomeViewModelTest](app/src/test/java/com/mastercoding/moviebox/presentation/home/HomeViewModelTest.kt)
covers the search → clear → paginate flow.)

**Backend over TMDb proxy.** Earlier versions hit TMDb directly. Switched to
our own Mongo-backed API so we can index on `titleLower` for fast
case-insensitive search and add fields the TMDb shape doesn't expose. The
DTO layer keeps `snake_case` field names so the migration was a single
`baseUrl` swap.

## Testing

```bash
./gradlew testDebugUnitTest
```

`HomeViewModelTest` covers init, debounced search, search → clear flow,
`loadMore` guards (endReached / search freeze / error), retry branches, and
state transitions. Uses a `MainDispatcherRule` to swap `Dispatchers.Main` for
a `StandardTestDispatcher` so `viewModelScope.launch` runs under virtual time.
Repository is mocked with MockK.

CI runs `lint`, `testDebugUnitTest`, and `assembleDebug` on every push to
`main` — the debug APK is uploaded as a workflow artifact for quick demoing.

## Backend

The API is a separate repo: [`moviebox-backend`](../moviebox-backend) —
Express + TypeScript + Prisma 5 + MongoDB Atlas, deployed on Render. See its
README for the seed script, endpoint shape, and deploy notes.

Live API: <https://moviebox-backend-g4sn.onrender.com/api/movies/popular?limit=3>
