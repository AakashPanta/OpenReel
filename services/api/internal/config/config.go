package config

import "os"

type Config struct {
	AppEnv            string
	Port              string
	PublicBaseURL     string
	DatabaseURL       string
	ValkeyAddr        string
	NatsURL           string
	KeycloakURL       string
	KeycloakRealm     string
	KeycloakClientID  string
	TusdEndpoint      string
	SeaweedFSBucket   string
	OpenSearchURL     string
	UmamiURL          string
}

func Load() Config {
	return Config{
		AppEnv:           env("APP_ENV", "development"),
		Port:             env("API_PORT", "8080"),
		PublicBaseURL:    env("PUBLIC_BASE_URL", "http://localhost:8080"),
		DatabaseURL:      env("DATABASE_URL", "postgres://openreel:openreel@postgres:5432/openreel?sslmode=disable"),
		ValkeyAddr:       env("VALKEY_ADDR", "valkey:6379"),
		NatsURL:          env("NATS_URL", "nats://nats:4222"),
		KeycloakURL:      env("KEYCLOAK_URL", "http://keycloak:8081"),
		KeycloakRealm:    env("KEYCLOAK_REALM", "openreel"),
		KeycloakClientID: env("KEYCLOAK_CLIENT_ID", "openreel-android"),
		TusdEndpoint:     env("TUSD_ENDPOINT", "http://tusd:1080/files"),
		SeaweedFSBucket:  env("SEAWEEDFS_BUCKET", "videos"),
		OpenSearchURL:    env("OPENSEARCH_URL", "http://opensearch:9200"),
		UmamiURL:         env("UMAMI_URL", "http://umami:3000"),
	}
}

func env(key, fallback string) string {
	value := os.Getenv(key)
	if value == "" {
		return fallback
	}
	return value
}
