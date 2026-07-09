package com.aspharier.doworkoutdaily.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf

enum class ThemeMode {
    AMOLED_BLACK,
    BLOSSOM_LIGHT,
    SYSTEM
}

val LocalThemeMode = compositionLocalOf { ThemeMode.AMOLED_BLACK }

private val AmoledColorScheme = darkColorScheme(
    primary = V2DarkAccent,
    onPrimary = V2DarkText,
    primaryContainer = V2DarkSurface2,
    onPrimaryContainer = V2DarkAccent,
    secondary = V2DarkTextDim,
    onSecondary = V2DarkBg,
    secondaryContainer = V2DarkSurface3,
    onSecondaryContainer = V2DarkText,
    background = V2DarkBg,
    onBackground = V2DarkText,
    surface = V2DarkSurface,
    onSurface = V2DarkText,
    surfaceVariant = V2DarkSurface2,
    onSurfaceVariant = V2DarkTextDim,
    outline = V2DarkBorder,
    error = V2DarkDanger,
    onError = V2DarkText
)

private val BlossomColorScheme = lightColorScheme(
    primary = V2LightAccent,
    onPrimary = V2LightSurface,
    primaryContainer = V2LightSurface2,
    onPrimaryContainer = V2LightAccent,
    secondary = V2LightTextDim,
    onSecondary = V2LightSurface,
    secondaryContainer = V2LightSurface3,
    onSecondaryContainer = V2LightText,
    background = V2LightBg,
    onBackground = V2LightText,
    surface = V2LightSurface,
    onSurface = V2LightText,
    surfaceVariant = V2LightSurface2,
    onSurfaceVariant = V2LightTextDim,
    outline = V2LightBorder,
    error = V2LightDanger,
    onError = V2LightSurface
)

@Composable
fun DoWorkoutDailyTheme(
    themeMode: ThemeMode = ThemeMode.AMOLED_BLACK,
    content: @Composable () -> Unit
) {
    val resolvedMode = when (themeMode) {
        ThemeMode.SYSTEM -> if (isSystemInDarkTheme()) ThemeMode.AMOLED_BLACK else ThemeMode.BLOSSOM_LIGHT
        else -> themeMode
    }

    val colorScheme = when (resolvedMode) {
        ThemeMode.AMOLED_BLACK -> AmoledColorScheme
        ThemeMode.BLOSSOM_LIGHT -> BlossomColorScheme
        else -> AmoledColorScheme
    }

    CompositionLocalProvider(LocalThemeMode provides resolvedMode) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}