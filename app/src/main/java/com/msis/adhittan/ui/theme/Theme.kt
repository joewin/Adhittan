package com.msis.adhittan.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = DeepMoss,
    secondary = WarmSand,
    tertiary = TransformativeTeal,
    background = DeepMoss,
    surface = SoftClay,
    onPrimary = OffWhite,
    onSecondary = DeepMoss,
    onTertiary = OffWhite,
    onBackground = OffWhite,
    onSurface = DeepMoss,
)

private val LightColorScheme = lightColorScheme(
    primary = DeepMoss,
    secondary = WarmSand,
    tertiary = TransformativeTeal,
    background = DeepMoss,
    surface = SoftClay,
    onPrimary = OffWhite,
    onSecondary = DeepMoss,
    onTertiary = OffWhite,
    onBackground = OffWhite,
    onSurface = DeepMoss,
)

@Composable
fun AdhittanTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // For now forced to our custom look mostly
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disable dynamic color to enforce Deep Moss branding
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb() // Match background
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Will need Type.kt if using custom fonts, defaulting for now
        content = content
    )
}
