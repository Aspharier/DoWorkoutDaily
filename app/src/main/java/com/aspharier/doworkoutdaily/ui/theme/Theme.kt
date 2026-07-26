package com.aspharier.doworkoutdaily.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Immutable
data class GrindColors(
    val cream: Color = Cream,
    val ink: Color = Ink,
    val acidLime: Color = AcidLime,
    val hypeMagenta: Color = HypeMagenta,
    val sky: Color = Sky,
    val tangerine: Color = Tangerine,
    val grape: Color = Grape,
    val stone: Color = Stone,
    val paper: Color = Paper
)

val LocalGrindColors = staticCompositionLocalOf { GrindColors() }

object GrindTheme {
    val colors: GrindColors
        @Composable
        get() = LocalGrindColors.current
}

private val GrindColorScheme = lightColorScheme(
    primary = AcidLime,
    onPrimary = Ink,
    secondary = HypeMagenta,
    onSecondary = Color.White,
    background = Cream,
    onBackground = Ink,
    surface = Cream,
    onSurface = Ink
)

@Composable
fun GrindTheme(content: @Composable () -> Unit) {
    val colors = GrindColors()
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Ink.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }
    CompositionLocalProvider(LocalGrindColors provides colors) {
        MaterialTheme(
            colorScheme = GrindColorScheme,
            typography = AppTypography,
            content = content
        )
    }
}