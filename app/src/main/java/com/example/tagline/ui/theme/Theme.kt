package com.example.tagline.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryCrimson,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryCrimsonDark,
    onPrimaryContainer = OnDark,
    secondary = SecondaryGold,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryGoldDark,
    onSecondaryContainer = OnDark,
    tertiary = Info,
    onTertiary = OnPrimary,
    background = BackgroundDark,
    onBackground = OnDark,
    surface = SurfaceDark,
    onSurface = OnDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = NeutralLighter,
    error = Error,
    onError = OnPrimary,
    outline = NeutralLight,
    outlineVariant = NeutralMedium
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryCrimson,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryCrimsonLight,
    onPrimaryContainer = OnLight,
    secondary = SecondaryGoldDark,
    onSecondary = OnLight,
    secondaryContainer = SecondaryGoldLight,
    onSecondaryContainer = OnLight,
    tertiary = Info,
    onTertiary = OnPrimary,
    background = BackgroundLight,
    onBackground = OnLight,
    surface = SurfaceLight,
    onSurface = OnLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = NeutralLighter,
    error = Error,
    onError = OnPrimary,
    outline = NeutralLighter,
    outlineVariant = SurfaceVariantLight
)

@Composable
fun TaglineTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
