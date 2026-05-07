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
    BLOSSOM_LIGHT
}

val LocalThemeMode = compositionLocalOf { ThemeMode.AMOLED_BLACK }

private val AmoledColorScheme = darkColorScheme(
    primary = AmoledPrimary,
    onPrimary = AmoledOnBackground,
    primaryContainer = AmoledCardVariant,
    onPrimaryContainer = AmoledPrimary,
    secondary = AmoledSecondary,
    onSecondary = AmoledOnBackground,
    secondaryContainer = AmoledCard,
    onSecondaryContainer = AmoledSecondary,
    background = AmoledBackground,
    onBackground = AmoledOnBackground,
    surface = AmoledSurface,
    onSurface = AmoledOnSurface,
    surfaceVariant = AmoledCard,
    onSurfaceVariant = AmoledOnSurfaceVariant,
    outline = AmoledOutline,
    error = AmoledError,
    onError = AmoledOnBackground
)

private val BlossomColorScheme = lightColorScheme(
    primary = BlossomPrimary,
    onPrimary = BlossomSurface,
    primaryContainer = BlossomCard,
    onPrimaryContainer = BlossomPrimaryVariant,
    secondary = BlossomSecondary,
    onSecondary = BlossomSurface,
    secondaryContainer = BlossomCardVariant,
    onSecondaryContainer = BlossomPrimaryVariant,
    background = BlossomBackground,
    onBackground = BlossomOnBackground,
    surface = BlossomSurface,
    onSurface = BlossomOnSurface,
    surfaceVariant = BlossomCardVariant,
    onSurfaceVariant = BlossomOnSurfaceVariant,
    outline = BlossomOutline,
    error = BlossomError,
    onError = BlossomSurface
)

@Composable
fun DoWorkoutDailyTheme(
    themeMode: ThemeMode = ThemeMode.AMOLED_BLACK,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeMode) {
        ThemeMode.AMOLED_BLACK -> AmoledColorScheme
        ThemeMode.BLOSSOM_LIGHT -> BlossomColorScheme
    }

    CompositionLocalProvider(LocalThemeMode provides themeMode) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}