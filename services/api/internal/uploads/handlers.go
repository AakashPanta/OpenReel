package uploads

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

func (h *Handler) CreateUpload(w http.ResponseWriter, r *http.Request) {
	var req CreateUploadRequest
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		common.WriteJSON(w, http.StatusBadRequest, map[string]string{"error": "invalid json body"})
		return
	}

	if req.FileName == "" || req.SizeBytes <= 0 {
		common.WriteJSON(w, http.StatusBadRequest, map[string]string{"error": "file_name and size_bytes are required"})
		return
	}

	common.WriteJSON(w, http.StatusCreated, h.service.Create(req))
}
