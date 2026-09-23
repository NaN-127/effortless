package com.effortless

import androidx.compose.ui.window.ComposeUIViewController
import com.effortless.presentation.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController {
    App()
}
