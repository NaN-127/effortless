package com.effortless.core.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import okhttp3.Interceptor

object AndroidNetworkConfig {
    private val _interceptors = mutableListOf<Interceptor>()
    val interceptors: List<Interceptor> get() = _interceptors

    fun addInterceptor(interceptor: Interceptor) {
        if (!_interceptors.contains(interceptor)) {
            _interceptors.add(interceptor)
        }
    }
}

actual fun createHttpEngine(): HttpClientEngine = OkHttp.create {
    config {
        AndroidNetworkConfig.interceptors.forEach { interceptor ->
            addInterceptor(interceptor)
        }
    }
}

