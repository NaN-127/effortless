package com.effortless.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val EffortlessPrimary = Color(0xFF6366F1) // Indigo accent
val EffortlessOnPrimary = Color(0xFFFFFFFF)
val EffortlessPrimaryContainer = Color(0xFFE0E7FF)
val EffortlessOnPrimaryContainer = Color(0xFF1E1B4B)

val EffortlessSecondary = Color(0xFF0EA5E9) // Sky blue
val EffortlessBackgroundDark = Color(0xFF0F172A) // Slate 900
val EffortlessSurfaceDark = Color(0xFF1E293B) // Slate 800
val EffortlessSurfaceVariantDark = Color(0xFF334155)

val EffortlessBackgroundLight = Color(0xFFF8FAFC)
val EffortlessSurfaceLight = Color(0xFFFFFFFF)
val EffortlessSurfaceVariantLight = Color(0xFFF1F5F9)

private val DarkColorScheme = darkColorScheme(
    primary = EffortlessPrimary,
    onPrimary = EffortlessOnPrimary,
    primaryContainer = EffortlessSurfaceVariantDark,
    onPrimaryContainer = Color(0xFFE2E8F0),
    secondary = EffortlessSecondary,
    background = EffortlessBackgroundDark,
    surface = EffortlessSurfaceDark,
    surfaceVariant = EffortlessSurfaceVariantDark
)

private val LightColorScheme = lightColorScheme(
    primary = EffortlessPrimary,
    onPrimary = EffortlessOnPrimary,
    primaryContainer = EffortlessPrimaryContainer,
    onPrimaryContainer = EffortlessOnPrimaryContainer,
    secondary = EffortlessSecondary,
    background = EffortlessBackgroundLight,
    surface = EffortlessSurfaceLight,
    surfaceVariant = EffortlessSurfaceVariantLight
)

@Composable
fun EffortlessTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
