package peertube

type LocalOAuthClient struct {
	ClientID     string `json:"client_id"`
	ClientSecret string `json:"client_secret"`
}

type TokenResponse struct {
	AccessToken  string `json:"access_token"`
	RefreshToken string `json:"refresh_token"`
	TokenType    string `json:"token_type"`
	ExpiresIn    int    `json:"expires_in"`
	Scope        string `json:"scope"`
}

type VideoListResponse struct {
	Total int           `json:"total"`
	Data  []VideoRecord `json:"data"`
}

type VideoRecord struct {
	ID          int            `json:"id"`
	UUID        string         `json:"uuid"`
	ShortUUID   string         `json:"shortUUID"`
	Name        string         `json:"name"`
	Description string         `json:"description"`
	Duration    int            `json:"duration"`
	Views       int            `json:"views"`
	Likes       int            `json:"likes"`
	Comments    int            `json:"comments"`
	NSFW        bool           `json:"nsfw"`
	PublishedAt string         `json:"publishedAt"`
	CreatedAt   string         `json:"createdAt"`
	UpdatedAt   string         `json:"updatedAt"`
	Channel     ChannelRecord  `json:"channel"`
	Account     AccountRecord  `json:"account"`
	Thumbnails  []ThumbnailRef `json:"thumbnails"`
	PreviewPath string         `json:"previewPath"`
}

type ChannelRecord struct {
	ID          int    `json:"id"`
	Name        string `json:"name"`
	DisplayName string `json:"displayName"`
}

type AccountRecord struct {
	ID          int    `json:"id"`
	Name        string `json:"name"`
	DisplayName string `json:"displayName"`
	Host        string `json:"host"`
}

type ThumbnailRef struct {
	Path string `json:"path"`
}

type SearchVideosParams struct {
	Start int
	Count int
	Sort  string
}

type InitResumableUploadRequest struct {
	ChannelID   int    `json:"channelId"`
	Filename    string `json:"filename"`
	Name        string `json:"name"`
	Description string `json:"description,omitempty"`
	Privacy     int    `json:"privacy,omitempty"`
}

type InitResumableUploadResponse struct {
	UploadID        string
	UploadURL       string
	HTTPStatus      int
	RawLocation     string
	PeerTubeVideoID int
	PeerTubeUUID    string
}
