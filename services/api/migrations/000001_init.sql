CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    keycloak_subject TEXT UNIQUE NOT NULL,
    username TEXT UNIQUE NOT NULL,
    display_name TEXT NOT NULL,
    bio TEXT NOT NULL DEFAULT '',
    avatar_url TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS videos (
    id UUID PRIMARY KEY,
    creator_id UUID NOT NULL REFERENCES users(id),
    title TEXT NOT NULL,
    caption TEXT NOT NULL DEFAULT '',
    status TEXT NOT NULL,
    visibility TEXT NOT NULL DEFAULT 'public',
    duration_seconds INTEGER NOT NULL DEFAULT 0,
    playback_url TEXT,
    thumbnail_url TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS upload_sessions (
    id UUID PRIMARY KEY,
    video_id UUID,
    provider TEXT NOT NULL DEFAULT 'tusd',
    provider_upload_id TEXT NOT NULL,
    status TEXT NOT NULL,
    file_name TEXT NOT NULL,
    content_type TEXT,
    size_bytes BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
