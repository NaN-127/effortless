package com.effortless.core.platform

/**
 * Platform runtime information.
 */
data class PlatformInfo(
    val osName: String,
    val osVersion: String,
    val deviceModel: String
)

/**
 * Expect declaration for retrieving platform runtime info.
 */
expect fun getPlatformInfo(): PlatformInfo
