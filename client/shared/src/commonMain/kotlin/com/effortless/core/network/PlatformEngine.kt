package com.effortless.core.network

import io.ktor.client.engine.HttpClientEngine

/**
 * Platform abstraction to supply the optimal Ktor HTTP engine:
 * - Android -> OkHttp
 * - iOS -> Darwin
 * - Desktop -> CIO
 */
expect fun createHttpEngine(): HttpClientEngine
