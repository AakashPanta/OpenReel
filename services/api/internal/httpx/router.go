package httpx

import (
	"net/http"
	"time"

	"github.com/AakashPanta/OpenReel/services/api/internal/common"
	"github.com/AakashPanta/OpenReel/services/api/internal/config"
	"github.com/AakashPanta/OpenReel/services/api/internal/feed"
	"github.com/AakashPanta/OpenReel/services/api/internal/uploads"
	"github.com/go-chi/chi/v5"
	"github.com/go-chi/chi/v5/middleware"
)

func NewRouter(cfg config.Config, feedService *feed.Service, uploadService *uploads.Service) http.Handler {
	r := chi.NewRouter()
	r.Use(middleware.RequestID)
	r.Use(middleware.RealIP)
	r.Use(middleware.Recoverer)
	r.Use(middleware.Timeout(30 * time.Second))

	feedHandler := feed.NewHandler(feedService)
	uploadHandler := uploads.NewHandler(uploadService)

	r.Get("/healthz", func(w http.ResponseWriter, r *http.Request) {
		common.WriteJSON(w, http.StatusOK, map[string]any{
			"status": "ok",
			"service": "openreel-api",
			"env": cfg.AppEnv,
		})
	})

	r.Route("/v1", func(r chi.Router) {
		r.Get("/feed", feedHandler.GetFeed)
		r.Post("/uploads/create", uploadHandler.CreateUpload)
	})

	return r
}
