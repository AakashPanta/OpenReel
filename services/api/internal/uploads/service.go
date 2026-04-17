package uploads

import (
	"context"
	"fmt"
	"strconv"
	"strings"
	"time"

	"github.com/AakashPanta/OpenReel/services/api/internal/config"
	"github.com/AakashPanta/OpenReel/services/api/internal/peertube"
	"github.com/google/uuid"
)

type Service struct {
	cfg      config.Config
	peerTube *peertube.Client
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
	Provider          string            `json:"provider"`
}

func NewService(cfg config.Config, peerTube *peertube.Client) *Service {
	return &Service{
		cfg:      cfg,
		peerTube: peerTube,
	}
}

func (s *Service) Create(ctx context.Context, req CreateUploadRequest) (CreateUploadResponse, error) {
	if !s.peerTube.Enabled() || s.cfg.PeerTubeUsername == "" || s.cfg.PeerTubePassword == "" || s.cfg.PeerTubeChannelID == "" {
		return s.mockCreate(req), nil
	}

	channelID, err := strconv.Atoi(s.cfg.PeerTubeChannelID)
	if err != nil {
		return CreateUploadResponse{}, fmt.Errorf("invalid PEERTUBE_CHANNEL_ID: %w", err)
	}

	token, err := s.login(ctx)
	if err != nil {
		return CreateUploadResponse{}, err
	}

	title := titleFromFilename(req.FileName)

	initResp, err := s.peerTube.InitializeResumableUpload(
		ctx,
		token.AccessToken,
		req.SizeBytes,
		req.ContentType,
		peertube.InitResumableUploadRequest{
			ChannelID: channelID,
			Filename:  req.FileName,
			Name:      title,
			Privacy:   1,
		},
	)
	if err != nil {
		return CreateUploadResponse{}, err
	}

	uploadID := initResp.UploadID
	if uploadID == "" {
		uploadID = uuid.NewString()
	}

	draftID := "peertube_draft_" + strings.ReplaceAll(uuid.NewString(), "-", "")[:12]

	return CreateUploadResponse{
		UploadID:     uploadID,
		UploadURL:    initResp.UploadURL,
		DraftVideoID: draftID,
		Status:       "uploading",
		ExpiresAt:    time.Now().UTC().Add(1 * time.Hour),
		TusHeaders: map[string]string{
			"Authorization":          "Bearer " + token.AccessToken,
			"X-Upload-Content-Length": fmt.Sprintf("%d", req.SizeBytes),
			"X-Upload-Content-Type":   req.ContentType,
			"Content-Type":            "application/octet-stream",
		},
		ProcessingWebhook: fmt.Sprintf("%s/internal/uploads/%s/complete", strings.TrimRight(s.cfg.PublicBaseURL, "/"), uploadID),
		Provider:          "peertube",
	}, nil
}

func (s *Service) login(ctx context.Context) (peertube.TokenResponse, error) {
	client, err := s.peerTube.GetLocalOAuthClient(ctx)
	if err != nil {
		return peertube.TokenResponse{}, err
	}

	return s.peerTube.PasswordLogin(
		ctx,
		client.ClientID,
		client.ClientSecret,
		s.cfg.PeerTubeUsername,
		s.cfg.PeerTubePassword,
		s.cfg.PeerTubeTokenScope,
	)
}

func (s *Service) mockCreate(req CreateUploadRequest) CreateUploadResponse {
	uploadID := uuid.NewString()
	draftID := "draft_" + strings.ReplaceAll(uuid.NewString(), "-", "")[:12]

	return CreateUploadResponse{
		UploadID:     uploadID,
		UploadURL:    fmt.Sprintf("%s/api/v1/videos/upload-resumable?upload_id=%s", "https://peertube.local", uploadID),
		DraftVideoID: draftID,
		Status:       "uploading",
		ExpiresAt:    time.Now().UTC().Add(24 * time.Hour),
		TusHeaders: map[string]string{
			"Content-Type":            "application/octet-stream",
			"X-Upload-Content-Length": fmt.Sprintf("%d", req.SizeBytes),
			"X-Upload-Content-Type":   req.ContentType,
		},
		ProcessingWebhook: fmt.Sprintf("%s/internal/uploads/%s/complete", strings.TrimRight(s.cfg.PublicBaseURL, "/"), uploadID),
		Provider:          "mock",
	}
}

func titleFromFilename(filename string) string {
	name := filename
	if dot := strings.LastIndexByte(name, '.'); dot > 0 {
		name = name[:dot]
	}
	name = strings.ReplaceAll(name, "-", " ")
	name = strings.ReplaceAll(name, "_", " ")
	name = strings.TrimSpace(name)
	if name == "" {
		return "OpenReel upload"
	}
	return name
}
