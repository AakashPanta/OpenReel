# OpenReel Android MVP

OpenReel is a mobile-first Android MVP built with Kotlin, Jetpack Compose, and Material 3.

## Included in this starter
- Premium short-video style home feed
- Explore / discovery screen
- Upload composer screen
- Notifications screen
- Creator profile screen
- Clean navigation structure
- Mock repository with production-friendly separation
- Theme + reusable architecture foundation

## Stack
- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose
- Lifecycle ViewModel + StateFlow

## Architecture
- `data/model` — UI/domain starter models
- `data/repository` — repository contract + mock implementation
- `ui/feed` — reel feed experience
- `ui/explore` — discovery/search surface
- `ui/upload` — upload composer flow
- `ui/notifications` — alerts/activity list
- `ui/profile` — creator profile and content grid
- `ui/navigation` — app destinations and nav host
- `ui/theme` — design system foundation

## What this project is
This is a polished Android MVP frontend with mocked data and backend-ready boundaries.

## What to wire next
1. Replace `MockOpenReelRepository` with Retrofit/Ktor backed data sources.
2. Add Media3/ExoPlayer for real HLS/MP4 playback.
3. Add auth and upload APIs.
4. Add Room/DataStore for local persistence.
5. Add admin/moderation and reporting flows.

## Build notes
- Compile SDK: 36
- Min SDK: 26
- Target SDK: 36
- JDK: 17

---

## Monorepo Scaffold

# OpenReel OSS Monorepo Scaffold

This scaffold adds the first backend and infra foundation for OpenReel without breaking the current Android app.

## Current repo strategy

Your Android app already exists at the repository root.
To avoid breaking the current build, this scaffold **adds backend and infra directories now** instead of moving Android into `apps/android` immediately.

A safe later migration path is:

1. keep the existing Android project at root until backend wiring is stable
2. add CI for backend services
3. move Android into `apps/android` only after the repo has a stable multi-project workflow

## Added directories

- `services/api` — minimal Go API scaffold
- `infra/docker` — local OSS stack bootstrapping
- `packages/contracts/openapi` — shared contract between Android and backend
- `.env.example` — starter environment variables

## What the API currently does

- `GET /healthz`
- `GET /v1/feed`
- `POST /v1/uploads/create`

The feed is mock-backed for now, but the contract is stable enough to start wiring the Android app.

## Local run

From `services/api`:

```bash
go mod tidy
go run ./cmd/server
```

Then test:

```bash
curl http://localhost:8080/healthz
curl "http://localhost:8080/v1/feed?tab=for_you"
curl -X POST http://localhost:8080/v1/uploads/create \
  -H "Content-Type: application/json" \
  -d '{"file_name":"sample.mp4","content_type":"video/mp4","size_bytes":12345678}'
```

## Docker stack

From `infra/docker`:

```bash
docker compose up --build
```

This brings up:

- PostgreSQL
- Valkey
- NATS
- SeaweedFS
- tusd
- OpenSearch
- OpenReel API

## Recommended next step

After these files are added, the next high-value step is:

1. connect Android `FeedRepository` to `GET /v1/feed`
2. connect Android upload flow to `POST /v1/uploads/create`
3. add database-backed feed rows and migrations beyond the current scaffold
4. add Keycloak JWT validation middleware
