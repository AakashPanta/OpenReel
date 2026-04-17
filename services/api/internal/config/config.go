package config

import "os"

type Config struct {
	AppEnv         string
	Port           string
	PublicBaseURL  string
	DatabaseURL    string
	ValkeyAddr     string
	NatsURL        string
	OpenSearchURL  string
	UmamiURL       string
	PeerTubeBaseURL string
	PeerTubeUsername string
	PeerTubePassword string
	PeerTubeChannelID string
	PeerTubeTokenScope string
}

func Load() Config {
	return Config{
		AppEnv:             env("APP_ENV", "development"),
		Port:               env("API_PORT", "8080"),
		PublicBaseURL:      env("PUBLIC_BASE_URL", "http://localhost:8080"),
		DatabaseURL:        env("DATABASE_URL", "postgres://openreel:openreel@postgres:5432/openreel?sslmode=disable"),
		ValkeyAddr:         env("VALKEY_ADDR", "valkey:6379"),
		NatsURL:            env("NATS_URL", "nats://nats:4222"),
		OpenSearchURL:      env("OPENSEARCH_URL", "http://opensearch:9200"),
		UmamiURL:           env("UMAMI_URL", "http://umami:3000"),
		PeerTubeBaseURL:    trimTrailingSlash(env("PEERTUBE_BASE_URL", "")),
		PeerTubeUsername:   env("PEERTUBE_USERNAME", ""),
		PeerTubePassword:   env("PEERTUBE_PASSWORD", ""),
		PeerTubeChannelID:  env("PEERTUBE_CHANNEL_ID", ""),
		PeerTubeTokenScope: env("PEERTUBE_TOKEN_SCOPE", "user"),
	}
}

func env(key, fallback string) string {
	value := os.Getenv(key)
	if value == "" {
		return fallback
	}
	return value
}

func trimTrailingSlash(value string) string {
	for len(value) > 0 && value[len(value)-1] == '/' {
		value = value[:len(value)-1]
	}
	return value
}
