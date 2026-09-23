package com.effortless.presentation

import androidx.compose.runtime.Composable
import com.effortless.core.di.appModules
import com.effortless.presentation.home.HomeScreen
import com.effortless.presentation.home.HomeViewModel
import com.effortless.presentation.theme.EffortlessTheme
import org.koin.compose.KoinApplication
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    KoinApplication(application = {
        modules(appModules)
    }) {
        EffortlessTheme {
            val viewModel = koinViewModel<HomeViewModel>()
            HomeScreen(viewModel = viewModel)
        }
    }
}
