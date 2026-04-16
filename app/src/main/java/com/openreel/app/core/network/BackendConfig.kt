package com.openreel.app.core.network

import com.openreel.app.BuildConfig

object BackendConfig {
    val baseUrl: String
        get() = BuildConfig.OPENREEL_API_BASE_URL
}
