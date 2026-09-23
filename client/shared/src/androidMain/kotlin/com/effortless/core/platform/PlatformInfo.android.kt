package com.effortless.core.platform

import android.os.Build

actual fun getPlatformInfo(): PlatformInfo = PlatformInfo(
    osName = "Android",
    osVersion = "API ${Build.VERSION.SDK_INT}",
    deviceModel = "${Build.MANUFACTURER} ${Build.MODEL}"
)
