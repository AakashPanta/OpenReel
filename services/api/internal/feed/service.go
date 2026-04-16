package feed

import "time"

type Service struct{}

type FeedResponse struct {
	Tab          string        `json:"tab"`
	Cursor       string        `json:"cursor,omitempty"`
	NextCursor   string        `json:"next_cursor,omitempty"`
	Items        []FeedItem    `json:"items"`
	Explaination []string      `json:"explanation"`
	GeneratedAt  time.Time     `json:"generated_at"`
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

func NewService() *Service {
	return &Service{}
}

func (s *Service) Get(tab string, cursor string) FeedResponse {
	if tab == "" {
		tab = "for_you"
	}

	return FeedResponse{
		Tab:        tab,
		Cursor:     cursor,
		NextCursor: "page_2",
		GeneratedAt: time.Now().UTC(),
		Explaination: []string{
			"Transparent scoring scaffold",
			"This response is currently mock-backed",
			"Replace with database-backed ranking in the next step",
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
