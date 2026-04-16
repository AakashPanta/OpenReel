package com.openreel.app.core

import com.openreel.app.core.network.BackendConfig
import com.openreel.app.data.remote.OpenReelApiFactory
import com.openreel.app.data.repository.BackendFeedRepository
import com.openreel.app.data.repository.BackendUploadRepository
import com.openreel.app.data.repository.MockOpenReelRepository

object AppContainer {
    private val mockRepository by lazy { MockOpenReelRepository() }
    private val apiService by lazy {
        OpenReelApiFactory.create(BackendConfig.baseUrl)
    }

    val mockOpenReelRepository: MockOpenReelRepository
        get() = mockRepository

    val feedRepository by lazy {
        BackendFeedRepository(
            api = apiService,
            fallback = mockRepository
        )
    }

    val uploadRepository by lazy {
        BackendUploadRepository(api = apiService)
    }
}
