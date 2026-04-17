# PeerTube Docker Stack for OpenReel

This directory vendors the official PeerTube Docker production stack into the OpenReel repo.

## OpenReel defaults already applied
- Hostname: openreel.duckdns.org
- Admin email: kimyuri466@gmail.com
- Postgres user: peertube

## Important
- The committed `.env` is a setup template.
- Replace these placeholder values before first start:
  - `YOUR_DB_PASSWORD`
  - `YOUR_PEERTUBE_SECRET`
  - `YOUR_ROOT_PASSWORD`
- Do not commit real secrets to the public repo.
- Choose your hostname carefully before first start.

## Required next step
Edit:
`infra/docker/peertube/.env`

Replace:
- `YOUR_DB_PASSWORD`
- `YOUR_PEERTUBE_SECRET`
- `YOUR_ROOT_PASSWORD`

## Run
```bash
cd infra/docker/peertube
docker compose up -d
```

## Logs
```bash
docker compose logs -f peertube
```

## OpenReel backend integration
Set:
```env
PEERTUBE_BASE_URL=https://openreel.duckdns.org
PEERTUBE_USERNAME=root
PEERTUBE_PASSWORD=YOUR_ROOT_PASSWORD
PEERTUBE_CHANNEL_ID=YOUR_CHANNEL_ID
PEERTUBE_TOKEN_SCOPE=user
```
