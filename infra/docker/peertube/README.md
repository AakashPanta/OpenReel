# PeerTube Docker Stack for OpenReel

This directory vendors the official PeerTube Docker production stack into the OpenReel repo.

## Important
- Choose your final hostname carefully before first start.
- Edit `.env` before running the stack.
- This stack is intentionally separate from `infra/docker/docker-compose.yml` to avoid service collisions with the rest of OpenReel.

## Required `.env` fields
Replace these placeholders in `infra/docker/peertube/.env`:
- <MY POSTGRES USERNAME>
- <MY POSTGRES PASSWORD>
- <MY DOMAIN>
- <MY EMAIL ADDRESS>
- <MY PEERTUBE SECRET>

## Suggested OpenReel usage
- Run PeerTube as the media/auth substrate.
- Keep OpenReel API as the adapter/ranking/mobile DTO layer.
- Point OpenReel API `PEERTUBE_BASE_URL` to your PeerTube base URL.

## Run
cd infra/docker/peertube
docker compose up -d

## Logs
docker compose logs -f peertube
