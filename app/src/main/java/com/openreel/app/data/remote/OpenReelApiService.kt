package com.openreel.app.data.remote

import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface OpenReelApiService {
    @GET("v1/feed")
    suspend fun getFeed(
        @Query("tab") tab: String,
        @Query("cursor") cursor: String? = null
    ): FeedResponseDto

    @POST("v1/uploads/create")
    suspend fun createUpload(
        @Body request: CreateUploadRequestDto
    ): CreateUploadResponseDto
}

object OpenReelApiFactory {
    fun create(baseUrl: String): OpenReelApiService {
        require(baseUrl.endsWith("/")) {
            "Base URL must end with a trailing slash: $baseUrl"
        }

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenReelApiService::class.java)
    }
}

data class FeedResponseDto(
    @SerializedName("tab")
    val tab: String,
    @SerializedName("cursor")
    val cursor: String?,
    @SerializedName("next_cursor")
    val nextCursor: String?,
    @SerializedName("explanation")
    val explanation: List<String>,
    @SerializedName("generated_at")
    val generatedAt: String,
    @SerializedName("items")
    val items: List<FeedItemDto>
)

data class FeedItemDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("caption")
    val caption: String,
    @SerializedName("playback_url")
    val playbackUrl: String,
    @SerializedName("thumbnail_url")
    val thumbnailUrl: String,
    @SerializedName("duration_sec")
    val durationSec: Int,
    @SerializedName("tags")
    val tags: List<String>,
    @SerializedName("creator")
    val creator: FeedCreatorDto,
    @SerializedName("stats")
    val stats: FeedStatsDto,
    @SerializedName("reasons")
    val reasons: List<ReasonLabelDto>
)

data class FeedCreatorDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("display_name")
    val displayName: String,
    @SerializedName("handle")
    val handle: String,
    @SerializedName("verified")
    val verified: Boolean
)

data class FeedStatsDto(
    @SerializedName("likes")
    val likes: Int,
    @SerializedName("comments")
    val comments: Int,
    @SerializedName("shares")
    val shares: Int,
    @SerializedName("saves")
    val saves: Int
)

data class ReasonLabelDto(
    @SerializedName("code")
    val code: String,
    @SerializedName("label")
    val label: String
)

data class CreateUploadRequestDto(
    @SerializedName("file_name")
    val fileName: String,
    @SerializedName("content_type")
    val contentType: String,
    @SerializedName("size_bytes")
    val sizeBytes: Long
)

data class CreateUploadResponseDto(
    @SerializedName("upload_id")
    val uploadId: String,
    @SerializedName("upload_url")
    val uploadUrl: String,
    @SerializedName("tus_headers")
    val tusHeaders: Map<String, String>,
    @SerializedName("draft_video_id")
    val draftVideoId: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("expires_at")
    val expiresAt: String,
    @SerializedName("processing_webhook")
    val processingWebhook: String
)
