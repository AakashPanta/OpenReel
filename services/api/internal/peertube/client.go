package peertube

import (
	"bytes"
	"context"
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"net/url"
	"strconv"
	"strings"
	"time"

	"github.com/AakashPanta/OpenReel/services/api/internal/config"
)

type Client struct {
	baseURL    string
	httpClient *http.Client
}

func NewClient(cfg config.Config) *Client {
	return &Client{
		baseURL: cfg.PeerTubeBaseURL,
		httpClient: &http.Client{
			Timeout: 20 * time.Second,
		},
	}
}

func (c *Client) Enabled() bool {
	return c != nil && c.baseURL != ""
}

func (c *Client) GetLocalOAuthClient(ctx context.Context) (LocalOAuthClient, error) {
	var out LocalOAuthClient
	if !c.Enabled() {
		return out, fmt.Errorf("peertube disabled")
	}

	req, err := http.NewRequestWithContext(ctx, http.MethodGet, c.baseURL+"/api/v1/oauth-clients/local", nil)
	if err != nil {
		return out, err
	}

	if err := c.doJSON(req, &out); err != nil {
		return out, err
	}

	return out, nil
}

func (c *Client) PasswordLogin(ctx context.Context, clientID, clientSecret, username, password, scope string) (TokenResponse, error) {
	var out TokenResponse
	if !c.Enabled() {
		return out, fmt.Errorf("peertube disabled")
	}

	form := url.Values{}
	form.Set("client_id", clientID)
	form.Set("client_secret", clientSecret)
	form.Set("grant_type", "password")
	form.Set("response_type", "code")
	form.Set("username", username)
	form.Set("password", password)
	if scope != "" {
		form.Set("scope", scope)
	}

	req, err := http.NewRequestWithContext(
		ctx,
		http.MethodPost,
		c.baseURL+"/api/v1/users/token",
		strings.NewReader(form.Encode()),
	)
	if err != nil {
		return out, err
	}

	req.Header.Set("Content-Type", "application/x-www-form-urlencoded")

	if err := c.doJSON(req, &out); err != nil {
		return out, err
	}

	return out, nil
}

func (c *Client) SearchVideos(ctx context.Context, accessToken string, params SearchVideosParams) (VideoListResponse, error) {
	var out VideoListResponse
	if !c.Enabled() {
		return out, fmt.Errorf("peertube disabled")
	}

	values := url.Values{}
	values.Set("start", strconv.Itoa(params.Start))
	values.Set("count", strconv.Itoa(params.Count))
	if params.Sort != "" {
		values.Set("sort", params.Sort)
	}
	values.Set("hasHLSFiles", "true")

	req, err := http.NewRequestWithContext(
		ctx,
		http.MethodGet,
		c.baseURL+"/api/v1/search/videos?"+values.Encode(),
		nil,
	)
	if err != nil {
		return out, err
	}

	if accessToken != "" {
		req.Header.Set("Authorization", "Bearer "+accessToken)
	}

	if err := c.doJSON(req, &out); err != nil {
		return out, err
	}

	return out, nil
}

func (c *Client) SubscriptionVideos(ctx context.Context, accessToken string, start, count int) (VideoListResponse, error) {
	var out VideoListResponse
	if !c.Enabled() {
		return out, fmt.Errorf("peertube disabled")
	}

	values := url.Values{}
	values.Set("start", strconv.Itoa(start))
	values.Set("count", strconv.Itoa(count))
	values.Set("hasHLSFiles", "true")

	req, err := http.NewRequestWithContext(
		ctx,
		http.MethodGet,
		c.baseURL+"/api/v1/users/me/subscriptions/videos?"+values.Encode(),
		nil,
	)
	if err != nil {
		return out, err
	}

	req.Header.Set("Authorization", "Bearer "+accessToken)

	if err := c.doJSON(req, &out); err != nil {
		return out, err
	}

	return out, nil
}

func (c *Client) InitializeResumableUpload(ctx context.Context, accessToken string, fileSize int64, contentType string, payload InitResumableUploadRequest) (InitResumableUploadResponse, error) {
	var out InitResumableUploadResponse
	if !c.Enabled() {
		return out, fmt.Errorf("peertube disabled")
	}

	body, err := json.Marshal(payload)
	if err != nil {
		return out, err
	}

	req, err := http.NewRequestWithContext(
		ctx,
		http.MethodPost,
		c.baseURL+"/api/v1/videos/upload-resumable",
		bytes.NewReader(body),
	)
	if err != nil {
		return out, err
	}

	req.Header.Set("Authorization", "Bearer "+accessToken)
	req.Header.Set("Content-Type", "application/json")
	req.Header.Set("X-Upload-Content-Length", strconv.FormatInt(fileSize, 10))
	req.Header.Set("X-Upload-Content-Type", contentType)

	resp, err := c.httpClient.Do(req)
	if err != nil {
		return out, err
	}
	defer resp.Body.Close()

	out.HTTPStatus = resp.StatusCode
	out.RawLocation = resp.Header.Get("Location")

	if resp.StatusCode < 200 || resp.StatusCode >= 300 {
		data, _ := io.ReadAll(io.LimitReader(resp.Body, 16*1024))
		return out, fmt.Errorf("peertube resumable init failed: status=%d body=%s", resp.StatusCode, strings.TrimSpace(string(data)))
	}

	if out.RawLocation != "" {
		if parsed, err := url.Parse(out.RawLocation); err == nil {
			out.UploadID = parsed.Query().Get("upload_id")
		}
	}

	var maybeBody struct {
		Video struct {
			ID        int    `json:"id"`
			UUID      string `json:"uuid"`
			ShortUUID string `json:"shortUUID"`
		} `json:"video"`
	}

	rawBody, _ := io.ReadAll(io.LimitReader(resp.Body, 64*1024))
	if len(rawBody) > 0 {
		_ = json.Unmarshal(rawBody, &maybeBody)
		out.PeerTubeVideoID = maybeBody.Video.ID
		out.PeerTubeUUID = maybeBody.Video.UUID
	}

	if out.UploadID != "" {
		out.UploadURL = fmt.Sprintf("%s/api/v1/videos/upload-resumable?upload_id=%s", c.baseURL, url.QueryEscape(out.UploadID))
	} else {
		out.UploadURL = c.baseURL + "/api/v1/videos/upload-resumable"
	}

	return out, nil
}

func (c *Client) doJSON(req *http.Request, out any) error {
	resp, err := c.httpClient.Do(req)
	if err != nil {
		return err
	}
	defer resp.Body.Close()

	if resp.StatusCode < 200 || resp.StatusCode >= 300 {
		data, _ := io.ReadAll(io.LimitReader(resp.Body, 16*1024))
		return fmt.Errorf("peertube request failed: method=%s path=%s status=%d body=%s",
			req.Method,
			req.URL.Path,
			resp.StatusCode,
			strings.TrimSpace(string(data)),
		)
	}

	if out == nil {
		return nil
	}

	return json.NewDecoder(resp.Body).Decode(out)
}
