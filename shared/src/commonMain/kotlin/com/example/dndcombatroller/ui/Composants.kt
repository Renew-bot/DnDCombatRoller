package com.example.dndcombatroller.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dndcombatroller.ui.theme.FormeBoutonPetit

/**
 * Bouton rectangulaire plat à deux états (sélectionné / non sélectionné), utilisé pour les
 * onglets, le choix avantage/désavantage et le type d'étape — remplace les pilules dégradées.
 */
@Composable
fun BoutonBascule(
    texte: String,
    selectionne: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    actif: Boolean = true,
) {
    Box(
        modifier = modifier
            .alpha(if (actif) 1f else 0.4f)
            .clip(FormeBoutonPetit)
            .background(if (selectionne) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
            .border(
                width = if (selectionne) 0.dp else 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = FormeBoutonPetit,
            )
            .clickable(enabled = actif, onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = texte,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (selectionne) FontWeight.Bold else FontWeight.Medium,
            color = if (selectionne) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
