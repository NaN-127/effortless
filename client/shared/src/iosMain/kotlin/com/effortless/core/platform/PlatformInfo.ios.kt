package com.effortless.core.platform

import platform.UIKit.UIDevice

actual fun getPlatformInfo(): PlatformInfo = PlatformInfo(
    osName = UIDevice.currentDevice.systemName(),
    osVersion = UIDevice.currentDevice.systemVersion(),
    deviceModel = UIDevice.currentDevice.model()
)
