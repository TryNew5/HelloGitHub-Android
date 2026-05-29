# HelloGitHub Android App

Kotlin + Jetpack Compose Android client for [HelloGitHub](https://hellogithub.com/), a platform for discovering beginner-friendly open source projects.

## Environment

| Tool | Version |
|------|---------|
| JDK | 21 (Android Studio JBR: `C:\Program Files\Android\Android Studio\jbr`) |
| Gradle | 8.9 |
| AGP | 8.7.3 |
| Kotlin | 2.1.0 |
| compileSdk / targetSdk | 36 |
| minSdk | 26 |

```bash
export JAVA_HOME="/c/Program Files/Android/Android Studio/jbr"
export ANDROID_HOME="$HOME/AppData/Local/Android/Sdk"
./gradlew assembleDebug
"$ANDROID_HOME/platform-tools/adb" install -r app/build/outputs/apk/debug/app-debug.apk
```

## Architecture

MVVM + Repository pattern with Koin DI.

```
UI (Compose) → ViewModel → Repository → ApiService (Retrofit)
                                          → PeriodicalWebService (OkHttp HTML scraping)
```

### DI (Koin)

- `NetworkModule.kt` — OkHttp client (with `Referer` header for anti-hotlinking), Retrofit, ApiService, Coil ImageLoader, PeriodicalWebService
- `AppModule.kt` — All ViewModels, Repositories, Application singleton

## API

Base URL: `https://api.hellogithub.com/v1/`

All endpoints are defined in `ApiService.kt`. **Critical**: Retrofit 2.11 does NOT allow empty `@GET("")` — must use `@GET("v1/")` with base URL `https://api.hellogithub.com/`.

Fields use **snake_case** (Python/FastAPI backend). Serialization: `@SerialName(...)`.

| Endpoint | Params | Notes |
|----------|--------|-------|
| `v1/` | `sort_by`, `rank_by`, `tid`, `year`, `month`, `page` | Home feed. Has `has_more` for pagination |
| `v1/repository/detail/{rid}` | — | Repo detail |
| `v1/search/` | `q`, `page` | Search |
| `v1/periodical/` | — | Periodical list (volumes only, no detail) |
| `v1/tag/` | `sort_by` | Topic tags |

### Periodical Detail — NO REST API

The REST API does **not** have a periodical detail endpoint (`v1/periodical/detail/{num}` returns 404). Periodical content is embedded in the Next.js SSR HTML at `https://hellogithub.com/periodical/volume/{num}` inside `<script id="__NEXT_DATA__" type="application/json">`.

`PeriodicalWebService.kt` uses OkHttp (blocking) with `withContext(Dispatchers.IO)` to scrape this JSON.

### Image Loading

Image server `img.hellogithub.com` requires `Referer: https://hellogithub.com/` header for anti-hotlinking. OkHttp interceptor adds this globally. Coil uses the same OkHttpClient.

## Key Files

### Theme
- `ui/theme/Color.kt` — Black-and-white minimalist palette. Primary = near-black `#1A1A1A`.
- `ui/theme/Theme.kt` — Material3 theme with light/dark support + status bar color.

### Data Layer
- `data/remote/ApiService.kt` — All Retrofit endpoints
- `data/remote/dto/HomeDtos.kt` — `HomeResponse`, `HomeItemDto`. `itemId` is OPTIONAL (search API omits it). Use `rid.ifEmpty { itemId }` for navigation IDs.
- `data/remote/dto/RepoDetailDtos.kt` — `RepositoryDto`. Has `title` (CN), `titleEn`, `summary`, `summaryEn`, `name`, `fullName`. Chinese-first fallback: `title.ifEmpty { titleEn ?: name.ifEmpty { fullName } }`.
- `data/remote/dto/PeriodicalDtos.kt` — List + volume DTOs
- `data/remote/dto/SearchDtos.kt` — `SearchResponse`
- `data/remote/PeriodicalWebService.kt` — HTML scraper for periodical detail
- `util/CoroutineUtils.kt` — `safeApiCall()`: like `runCatching` but **re-throws `CancellationException`** (critical for coroutine cancellation)

### Repositories
- `data/repository/HomeRepository.kt` — `getFeed(page=?, rankBy=?, year=?, month=?)`
- `data/repository/SearchRepository.kt`
- `data/repository/RepoDetailRepository.kt`
- `data/repository/PeriodicalRepository.kt` — `getList()` + `getVolume(num)`

### UI Screens
All screens in `ui/`:
- `feed/FeedScreen.kt` + `FeedViewModel.kt` — Infinite scroll with `derivedStateOf` bottom detection
- `search/SearchScreen.kt` + `SearchViewModel.kt` — 300ms debounce, crash-safe snapshot state
- `detail/DetailScreen.kt` + `DetailViewModel.kt` — Star chart, markdown, project name
- `periodical/PeriodicalScreen.kt` + `PeriodicalViewModel.kt` — Issue picker → category groups
- `rank/RankScreen.kt` + `RankViewModel.kt` — Week/month/year periods, month sub-filters
- `settings/SettingsScreen.kt` + `ThemeViewModel.kt` — Light/dark/system theme
- `navigation/AppNavGraph.kt` — All routes
- `navigation/BottomNavBar.kt` — 5-tab bottom nav
- `components/ProjectCard.kt` — Shared feed/search card (0 elevation, 12dp padding)
- `components/TopicScrollBar.kt` — Horizontal tag chips
- `components/LanguageBadge.kt`, `StarChart.kt`, `TagRow.kt`, `MarkdownView.kt`, `SkeletonProjectCard.kt`

## Critical Patterns & Gotchas

### 1. Compose Snapshot State Race → ClassCastException CRASH

**NEVER** use `(uiState as Success).results` after `when (uiState)`. The state can change mid-composition.

```kotlin
// ❌ WILL CRASH
when (uiState) {
    is Success -> { items((uiState as Success).results) }
}

// ✅ SAFE
val state = uiState  // snapshot read ONCE
when (state) {
    is Success -> { items(state.results) }
}
```

### 2. CancellationException must propagate

All repositories and `PeriodicalWebService` use `safeApiCall()` from `util/CoroutineUtils.kt`.
Standard `runCatching` catches `CancellationException`, causing stale coroutines to race with new ones.

```kotlin
// ❌ CancellationException swallowed → stale coroutines → crash
suspend fun search(): Result<T> = runCatching { api.call() }

// ✅ CancellationException propagates → proper cancellation
suspend fun search(): Result<T> = safeApiCall { api.call() }
```

### 3. OkHttp on coroutines

`OkHttpClient.newCall().execute()` is **blocking**. Must wrap with `withContext(Dispatchers.IO)`.
Retrofit suspend functions handle this automatically.

### 4. LazyColumn MUST have keys

All `items()/itemsIndexed()` calls use `key = { it.rid.ifEmpty { it.itemId }.ifEmpty { it.fullName } }` to prevent Compose crashes during rapid data changes.

### 5. Search debounce

300ms debounce via `delay(300)` in coroutine. `searchJob?.cancel()` before launching new search.
With `safeApiCall()`, cancelled coroutines properly exit — CancellationException from `delay()` propagates.

### 6. API response field mapping

- Search: `rid` present, `item_id` may be empty → navigate with `item.rid.ifEmpty { item.itemId }`
- Ranking: uses `clicks_total` NOT `stars` (ranking API doesn't return star count)
- Feed: has `has_more`, `page` for pagination
- Detail: `title` (CN), `titleEn`, `summary` (CN), `summaryEn`, `name`, `fullName`
- `author` field is always present (author name, not null)

## Ranking Period/Sub-filter Logic

- Weekly ("本周"): `rank_by=weekly`, no sub-filter
- Monthly ("本月"): `rank_by=monthly`, sub-filter = 12 recent `YearMonth`s (e.g. "2026年5月"). Sends `year` + `month` params.
- Yearly ("年度"): `rank_by=yearly`, sub-filter = years (2026-2021). Sends `year` param only.

`RankViewModel` has `YearMonth(year, month)` data class. `selectPeriod()` resets sub-filter appropriately.

## Physical Device

ADB device ID: `f90fe7da`. Connected via USB.
