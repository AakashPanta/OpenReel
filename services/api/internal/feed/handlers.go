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
	resp, err := h.service.Get(
		r.Context(),
		r.URL.Query().Get("tab"),
		r.URL.Query().Get("cursor"),
	)
	if err != nil {
		common.WriteJSON(w, http.StatusBadGateway, map[string]string{
			"error": err.Error(),
		})
		return
	}

	common.WriteJSON(w, http.StatusOK, resp)
}
