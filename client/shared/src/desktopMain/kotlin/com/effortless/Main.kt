package com.effortless

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.effortless.presentation.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Effortless — AI Voice Translation Keyboard",
        state = rememberWindowState(width = 480.dp, height = 750.dp)
    ) {
        App()
    }
}
