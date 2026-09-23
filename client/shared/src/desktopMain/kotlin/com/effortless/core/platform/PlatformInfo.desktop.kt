package com.effortless.core.platform

actual fun getPlatformInfo(): PlatformInfo = PlatformInfo(
    osName = System.getProperty("os.name") ?: "Desktop JVM",
    osVersion = System.getProperty("os.version") ?: "Unknown",
    deviceModel = "JVM (Java ${System.getProperty("java.version")})"
)
