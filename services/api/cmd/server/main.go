package main

import (
	"context"
	"log"
	"net/http"
	"os/signal"
	"syscall"
	"time"

	"github.com/AakashPanta/OpenReel/services/api/internal/auth"
	"github.com/AakashPanta/OpenReel/services/api/internal/config"
	"github.com/AakashPanta/OpenReel/services/api/internal/feed"
	"github.com/AakashPanta/OpenReel/services/api/internal/httpx"
	"github.com/AakashPanta/OpenReel/services/api/internal/peertube"
	"github.com/AakashPanta/OpenReel/services/api/internal/uploads"
)

func main() {
	cfg := config.Load()

	peerTubeClient := peertube.NewClient(cfg)

	authService := auth.NewService(cfg, peerTubeClient)
	feedService := feed.NewService(cfg, peerTubeClient)
	uploadService := uploads.NewService(cfg, peerTubeClient)

	router := httpx.NewRouter(cfg, authService, feedService, uploadService)

	srv := &http.Server{
		Addr:              ":" + cfg.Port,
		Handler:           router,
		ReadHeaderTimeout: 10 * time.Second,
	}

	ctx, stop := signal.NotifyContext(context.Background(), syscall.SIGINT, syscall.SIGTERM)
	defer stop()

	go func() {
		log.Printf("openreel api listening on :%s", cfg.Port)
		if err := srv.ListenAndServe(); err != nil && err != http.ErrServerClosed {
			log.Fatalf("server failed: %v", err)
		}
	}()

	<-ctx.Done()

	shutdownCtx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()

	if err := srv.Shutdown(shutdownCtx); err != nil {
		log.Printf("shutdown error: %v", err)
	}
}
