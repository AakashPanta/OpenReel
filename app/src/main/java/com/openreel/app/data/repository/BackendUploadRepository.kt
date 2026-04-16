package com.openreel.app.data.repository

import com.openreel.app.data.model.UploadSession
import com.openreel.app.data.remote.CreateUploadRequestDto
import com.openreel.app.data.remote.OpenReelApiService

class BackendUploadRepository(
    private val api: OpenReelApiService
) {
    suspend fun createUploadSession(
        fileName: String,
        contentType: String,
        sizeBytes: Long
    ): Result<UploadSession> {
        return runCatching {
            val response = api.createUpload(
                CreateUploadRequestDto(
                    fileName = fileName,
                    contentType = contentType,
                    sizeBytes = sizeBytes
                )
            )

            UploadSession(
                uploadId = response.uploadId,
                uploadUrl = response.uploadUrl,
                draftVideoId = response.draftVideoId,
                status = response.status,
                expiresAt = response.expiresAt,
                processingWebhook = response.processingWebhook,
                tusHeaders = response.tusHeaders
            )
        }
    }
}
