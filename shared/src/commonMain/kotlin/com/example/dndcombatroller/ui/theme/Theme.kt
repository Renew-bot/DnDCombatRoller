package com.example.dndcombatroller.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = ArtlineBleu,
    onPrimary = Color.White,
    secondary = ArtlineRose,
    onSecondary = Color.White,
    background = ArtlineGris,
    onBackground = ArtlineBleuNuit,
    surface = Color.White,
    onSurface = ArtlineBleuNuit,
)

private val DarkColors = darkColorScheme(
    primary = ArtlineBleu,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1A0FA0),
    onPrimaryContainer = ArtlineGris,
    secondary = ArtlineRose,
    onSecondary = ArtlineBleuNuit,
    secondaryContainer = Color(0xFF3D003D),
    onSecondaryContainer = ArtlineRose,
    tertiary = ArtlineVert,
    onTertiary = ArtlineBleuNuit,
    tertiaryContainer = Color(0xFF003D2A),
    onTertiaryContainer = ArtlineVert,
    surface = ArtlineBleuNuit,
    onSurface = ArtlineGris,
    background = Color(0xFF000E44),
    onBackground = ArtlineGris,
    surfaceVariant = Color(0xFF0A2480),
    onSurfaceVariant = Color(0xFFA0AECC),
    outline = Color(0xFF3A50B0),
    outlineVariant = Color(0xFF1E3080),
    error = ArtlineOrange,
    onError = Color.White,
)

@Composable
fun DnDCombatRollerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content,
    )
}
