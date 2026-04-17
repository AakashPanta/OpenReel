package feed

import (
	"context"
	"fmt"
	"strconv"
	"strings"
	"time"

	"github.com/AakashPanta/OpenReel/services/api/internal/config"
	"github.com/AakashPanta/OpenReel/services/api/internal/peertube"
)

type Service struct {
	cfg      config.Config
	peerTube *peertube.Client
}

type FeedResponse struct {
	Tab         string        `json:"tab"`
	Cursor      string        `json:"cursor,omitempty"`
	NextCursor  string        `json:"next_cursor,omitempty"`
	Items       []FeedItem    `json:"items"`
	Explanation []string      `json:"explanation"`
	GeneratedAt time.Time     `json:"generated_at"`
}

type FeedItem struct {
	ID          string        `json:"id"`
	Title       string        `json:"title"`
	Caption     string        `json:"caption"`
	PlaybackURL string        `json:"playback_url"`
	Thumbnail   string        `json:"thumbnail_url"`
	DurationSec int           `json:"duration_sec"`
	Tags        []string      `json:"tags"`
	Creator     FeedCreator   `json:"creator"`
	Stats       FeedStats     `json:"stats"`
	Reasons     []ReasonLabel `json:"reasons"`
}

type FeedCreator struct {
	ID          string `json:"id"`
	DisplayName string `json:"display_name"`
	Handle      string `json:"handle"`
	Verified    bool   `json:"verified"`
}

type FeedStats struct {
	Likes    int `json:"likes"`
	Comments int `json:"comments"`
	Shares   int `json:"shares"`
	Saves    int `json:"saves"`
}

type ReasonLabel struct {
	Code  string `json:"code"`
	Label string `json:"label"`
}

func NewService(cfg config.Config, peerTube *peertube.Client) *Service {
	return &Service{
		cfg:      cfg,
		peerTube: peerTube,
	}
}

func (s *Service) Get(ctx context.Context, tab, cursor string) (FeedResponse, error) {
	if tab == "" {
		tab = "for_you"
	}

	start := 0
	if cursor != "" {
		if parsed, err := strconv.Atoi(cursor); err == nil && parsed >= 0 {
			start = parsed
		}
	}

	if !s.peerTube.Enabled() || s.cfg.PeerTubeUsername == "" || s.cfg.PeerTubePassword == "" {
		return s.mockFeed(tab, cursor), nil
	}

	token, err := s.login(ctx)
	if err != nil {
		return s.mockFeed(tab, cursor), nil
	}

	var videos peertube.VideoListResponse

	switch tab {
	case "following":
		videos, err = s.peerTube.SubscriptionVideos(ctx, token.AccessToken, start, 10)
	default:
		videos, err = s.peerTube.SearchVideos(ctx, token.AccessToken, peertube.SearchVideosParams{
			Start: start,
			Count: 10,
			Sort:  "-trending",
		})
	}

	if err != nil {
		return s.mockFeed(tab, cursor), nil
	}

	items := make([]FeedItem, 0, len(videos.Data))
	for _, video := range videos.Data {
		items = append(items, mapVideoToFeedItem(s.cfg.PeerTubeBaseURL, tab, video))
	}

	nextCursor := ""
	if len(videos.Data) > 0 && (start+len(videos.Data)) < videos.Total {
		nextCursor = strconv.Itoa(start + len(videos.Data))
	}

	explanation := []string{
		"PeerTube-backed feed adapter",
		"OpenReel is shaping the mobile payload on top of PeerTube",
	}

	if tab == "following" {
		explanation = append(explanation, "Following tab is sourced from PeerTube subscription videos")
	} else {
		explanation = append(explanation, "For You is currently candidate-backed by PeerTube search/trending")
	}

	return FeedResponse{
		Tab:         tab,
		Cursor:      cursor,
		NextCursor:  nextCursor,
		Items:       items,
		Explanation: explanation,
		GeneratedAt: time.Now().UTC(),
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

func mapVideoToFeedItem(baseURL, tab string, video peertube.VideoRecord) FeedItem {
	videoID := video.UUID
	if videoID == "" {
		videoID = video.ShortUUID
	}
	if videoID == "" {
		videoID = strconv.Itoa(video.ID)
	}

	title := strings.TrimSpace(video.Name)
	if title == "" {
		title = "Untitled video"
	}

	caption := strings.TrimSpace(video.Description)
	if caption == "" {
		caption = "Imported from PeerTube through the OpenReel adapter."
	}

	thumbnailURL := ""
	if len(video.Thumbnails) > 0 && video.Thumbnails[0].Path != "" {
		if strings.HasPrefix(video.Thumbnails[0].Path, "http://") || strings.HasPrefix(video.Thumbnails[0].Path, "https://") {
			thumbnailURL = video.Thumbnails[0].Path
		} else {
			thumbnailURL = strings.TrimRight(baseURL, "/") + video.Thumbnails[0].Path
		}
	} else if video.PreviewPath != "" {
		if strings.HasPrefix(video.PreviewPath, "http://") || strings.HasPrefix(video.PreviewPath, "https://") {
			thumbnailURL = video.PreviewPath
		} else {
			thumbnailURL = strings.TrimRight(baseURL, "/") + video.PreviewPath
		}
	}

	creatorHandle := video.Channel.Name
	if creatorHandle == "" {
		creatorHandle = video.Account.Name
	}
	if creatorHandle == "" {
		creatorHandle = "creator"
	}
	if !strings.HasPrefix(creatorHandle, "@") {
		creatorHandle = "@" + creatorHandle
	}

	creatorName := video.Channel.DisplayName
	if creatorName == "" {
		creatorName = video.Account.DisplayName
	}
	if creatorName == "" {
		creatorName = creatorHandle
	}

	reasons := []ReasonLabel{
		{Code: "peertube_source", Label: "Fetched from PeerTube"},
		}

	if tab == "following" {
		reasons = append(reasons, ReasonLabel{Code: "subscription_graph", Label: "From your subscriptions"})
	} else {
		reasons = append(reasons, ReasonLabel{Code: "candidate_pool", Label: "From search/trending candidates"})
	}

	playbackURL := fmt.Sprintf("https://cdn.openreel.local/videos/%s/master.m3u8", videoID)

	return FeedItem{
		ID:          videoID,
		Title:       title,
		Caption:     caption,
		PlaybackURL: playbackURL,
		Thumbnail:   thumbnailURL,
		DurationSec: video.Duration,
		Tags:        inferTags(title, caption),
		Creator: FeedCreator{
			ID:          strconv.Itoa(video.Channel.ID),
			DisplayName: creatorName,
			Handle:      creatorHandle,
			Verified:    false,
		},
		Stats: FeedStats{
			Likes:    video.Likes,
			Comments: video.Comments,
			Shares:   0,
			Saves:    0,
		},
		Reasons: reasons,
	}
}

func inferTags(title, caption string) []string {
	text := strings.ToLower(title + " " + caption)
	tags := make([]string, 0, 3)

	appendIfContains := func(code, needle string) {
		if strings.Contains(text, needle) && len(tags) < 3 {
			tags = append(tags, code)
		}
	}

	appendIfContains("opensource", "open")
	appendIfContains("video", "video")
	appendIfContains("mobile", "mobile")
	appendIfContains("android", "android")
	appendIfContains("design", "design")
	appendIfContains("creator", "creator")

	if len(tags) == 0 {
		tags = []string{"peertube", "openreel", "video"}
	}

	return tags
}

func (s *Service) mockFeed(tab, cursor string) FeedResponse {
	return FeedResponse{
		Tab:         tab,
		Cursor:      cursor,
		NextCursor:  "10",
		GeneratedAt: time.Now().UTC(),
		Explanation: []string{
			"Transparent scoring scaffold",
			"PeerTube is not configured yet, so this response is mock-backed",
			"Set PEERTUBE_BASE_URL, PEERTUBE_USERNAME, PEERTUBE_PASSWORD, and PEERTUBE_CHANNEL_ID to enable the adapter",
		},
		Items: []FeedItem{
			{
				ID:          "vid_001",
				Title:       "Transparent feed ranking for short videos",
				Caption:     "OpenReel scores reels using recency, completion, affinity, and topic match with operator-visible weights.",
				PlaybackURL: "https://cdn.openreel.local/videos/vid_001/master.m3u8",
				Thumbnail:   "https://cdn.openreel.local/videos/vid_001/thumb.jpg",
				DurationSec: 38,
				Tags:        []string{"opensource", "ranking", "productdesign"},
				Creator: FeedCreator{
					ID:          "creator_001",
					DisplayName: "Nova Studio",
					Handle:      "@novastudio",
					Verified:    true,
				},
				Stats: FeedStats{Likes: 42800, Comments: 2100, Shares: 840, Saves: 5400},
				Reasons: []ReasonLabel{
					{Code: "follow_affinity", Label: "Strong creator affinity"},
					{Code: "topic_match", Label: "Matches your interest tags"},
					{Code: "completion_rate", Label: "High completion in similar viewers"},
				},
			},
			{
				ID:          "vid_002",
				Title:       "Compose motion patterns for reel products",
				Caption:     "Mobile-first motion is about clarity, rhythm, and scroll confidence.",
				PlaybackURL: "https://cdn.openreel.local/videos/vid_002/master.m3u8",
				Thumbnail:   "https://cdn.openreel.local/videos/vid_002/thumb.jpg",
				DurationSec: 24,
				Tags:        []string{"compose", "ux", "motion"},
				Creator: FeedCreator{
					ID:          "creator_002",
					DisplayName: "Kai Motion",
					Handle:      "@kaimotion",
					Verified:    true,
				},
				Stats: FeedStats{Likes: 18300, Comments: 664, Shares: 310, Saves: 2000},
				Reasons: []ReasonLabel{
					{Code: "recency", Label: "Fresh upload"},
					{Code: "engagement", Label: "Trending in your topic cluster"},
				},
			},
		},
	}
}
