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

