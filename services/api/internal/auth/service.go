package auth

import (
	"context"
	"fmt"

	"github.com/AakashPanta/OpenReel/services/api/internal/config"
	"github.com/AakashPanta/OpenReel/services/api/internal/peertube"
)

type Service struct {
	cfg      config.Config
	peerTube *peertube.Client
}

type LoginRequest struct {
	Username string `json:"username"`
	Password string `json:"password"`
}

type LoginResponse struct {
	Provider     string `json:"provider"`
	AccessToken  string `json:"access_token"`
	RefreshToken string `json:"refresh_token"`
	TokenType    string `json:"token_type"`
	ExpiresIn    int    `json:"expires_in"`
	Scope        string `json:"scope"`
}

func NewService(cfg config.Config, peerTube *peertube.Client) *Service {
	return &Service{
		cfg:      cfg,
		peerTube: peerTube,
	}
}

func (s *Service) Login(ctx context.Context, req LoginRequest) (LoginResponse, error) {
	if !s.peerTube.Enabled() {
		return LoginResponse{}, fmt.Errorf("peertube is not configured")
	}

	username := req.Username
	if username == "" {
		username = s.cfg.PeerTubeUsername
	}

	password := req.Password
	if password == "" {
		password = s.cfg.PeerTubePassword
	}

	if username == "" || password == "" {
		return LoginResponse{}, fmt.Errorf("username and password are required")
	}

	client, err := s.peerTube.GetLocalOAuthClient(ctx)
	if err != nil {
		return LoginResponse{}, err
	}

	token, err := s.peerTube.PasswordLogin(
		ctx,
		client.ClientID,
		client.ClientSecret,
		username,
		password,
		s.cfg.PeerTubeTokenScope,
	)
	if err != nil {
		return LoginResponse{}, err
	}

	return LoginResponse{
		Provider:     "peertube",
		AccessToken:  token.AccessToken,
		RefreshToken: token.RefreshToken,
		TokenType:    token.TokenType,
		ExpiresIn:    token.ExpiresIn,
		Scope:        token.Scope,
	}, nil
}
