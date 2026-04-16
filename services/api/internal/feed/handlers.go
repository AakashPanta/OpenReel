package feed

import (
	"net/http"

	"github.com/AakashPanta/OpenReel/services/api/internal/common"
)

type Handler struct {
	service *Service
}

func NewHandler(service *Service) *Handler {
	return &Handler{service: service}
}

func (h *Handler) GetFeed(w http.ResponseWriter, r *http.Request) {
	common.WriteJSON(w, http.StatusOK, h.service.Get(r.URL.Query().Get("tab"), r.URL.Query().Get("cursor")))
}
