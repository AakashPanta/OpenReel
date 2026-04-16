package uploads

import (
	"fmt"
	"strings"
	"time"

	"github.com/AakashPanta/OpenReel/services/api/internal/config"
	"github.com/google/uuid"
)

type Service struct {
	cfg config.Config
}

type CreateUploadRequest struct {
	FileName    string `json:"file_name"`
	ContentType string `json:"content_type"`
	SizeBytes   int64  `json:"size_bytes"`
}

type CreateUploadResponse struct {
	UploadID          string            `json:"upload_id"`
	UploadURL         string            `json:"upload_url"`
	TusHeaders        map[string]string `json:"tus_headers"`
	DraftVideoID      string            `json:"draft_video_id"`
	Status            string            `json:"status"`
	ExpiresAt         time.Time         `json:"expires_at"`
	ProcessingWebhook string            `json:"processing_webhook"`
}

func NewService(cfg config.Config) *Service {
	return &Service{cfg: cfg}
}

func (s *Service) Create(req CreateUploadRequest) CreateUploadResponse {
	uploadID := uuid.NewString()
	draftID := "draft_" + strings.ReplaceAll(uuid.NewString(), "-", "")[:12]

	return CreateUploadResponse{
		UploadID:     uploadID,
		UploadURL:    fmt.Sprintf("%s/%s", strings.TrimRight(s.cfg.TusdEndpoint, "/"), uploadID),
		DraftVideoID: draftID,
		Status:       "uploading",
		ExpiresAt:    time.Now().UTC().Add(24 * time.Hour),
		TusHeaders: map[string]string{
			"Upload-Length": fmt.Sprintf("%d", req.SizeBytes),
			"Tus-Resumable": "1.0.0",
		},
		ProcessingWebhook: fmt.Sprintf("%s/internal/uploads/%s/complete", strings.TrimRight(s.cfg.PublicBaseURL, "/"), uploadID),
	}
}
