package auth

import (
	"encoding/json"
	"net/http"

	"github.com/AakashPanta/OpenReel/services/api/internal/common"
)

type Handler struct {
	service *Service
}

func NewHandler(service *Service) *Handler {
	return &Handler{service: service}
}

func (h *Handler) Login(w http.ResponseWriter, r *http.Request) {
	var req LoginRequest
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		common.WriteJSON(w, http.StatusBadRequest, map[string]string{
			"error": "invalid json body",
		})
		return
	}

	resp, err := h.service.Login(r.Context(), req)
	if err != nil {
		common.WriteJSON(w, http.StatusBadGateway, map[string]string{
			"error": err.Error(),
		})
		return
	}

	common.WriteJSON(w, http.StatusOK, resp)
}
