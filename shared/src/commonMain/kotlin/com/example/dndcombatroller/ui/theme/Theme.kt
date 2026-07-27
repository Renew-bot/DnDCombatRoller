package com.example.dndcombatroller.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ParcheminColors = lightColorScheme(
    primary = CuirBordeaux,
    onPrimary = Color.White,
    primaryContainer = ParcheminCarte,
    onPrimaryContainer = EncreTexte,
    secondary = OrResultat,
    onSecondary = Color.White,
    secondaryContainer = ParcheminCarte,
    onSecondaryContainer = EncreTexte,
    tertiary = VertSauge,
    onTertiary = Color.White,
    tertiaryContainer = ParcheminCarte,
    onTertiaryContainer = VertSauge,
    background = ParcheminFond,
    onBackground = EncreTexte,
    surface = ParcheminCarte,
    onSurface = EncreTexte,
    surfaceVariant = ParcheminCarte,
    onSurfaceVariant = EncreTexteSecondaire,
    outline = ParcheminBordure,
    outlineVariant = ParcheminBordure,
    error = BriqueErreur,
    onError = Color.White,
)

@Composable
fun DnDCombatRollerTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = ParcheminColors,
        typography = Typography,
        content = content,
    )
}
