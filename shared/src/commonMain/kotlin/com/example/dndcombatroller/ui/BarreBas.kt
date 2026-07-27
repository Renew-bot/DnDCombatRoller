package com.example.dndcombatroller.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dndcombatroller.ui.theme.FormeBouton

@Composable
fun BarreBas(
    degatsDuTour: Int,
    onAnnuler: () -> Unit,
    onRefaire: () -> Unit,
    onFinDuTour: () -> Unit,
    modifier: Modifier = Modifier,
    modePortrait: Boolean = false,
    onLancer: (() -> Unit)? = null,
    lancerActif: Boolean = false,
) {
    var confirmerFinDuTour by remember { mutableStateOf(false) }

    if (confirmerFinDuTour) {
        AlertDialog(
            onDismissRequest = { confirmerFinDuTour = false },
            title = { Text("Fin du tour") },
            text = {
                val texte = if (degatsDuTour > 0)
                    "Les $degatsDuTour dégâts du tour seront remis à zéro."
                else
                    "Réinitialiser la sélection ?"
                Text(texte)
            },
            confirmButton = {
                Button(onClick = { onFinDuTour(); confirmerFinDuTour = false }, shape = FormeBouton) {
                    Text("Confirmer")
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmerFinDuTour = false }) {
                    Text("Annuler")
                }
            },
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (modePortrait && onLancer != null) {
            BoutonContourCompact(
                texte = "↩",
                description = "Annuler la dernière action",
                onClick = onAnnuler,
            )
            BoutonContourCompact(
                texte = "↪",
                description = "Rétablir l'action annulée",
                onClick = onRefaire,
            )
            BoutonFinDuTour(
                onClick = { confirmerFinDuTour = true },
                modifier = Modifier.height(40.dp),
                compact = true,
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .alpha(if (lancerActif) 1f else 0.35f)
                    .clip(FormeBouton)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable(enabled = lancerActif, onClick = onLancer),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "LANCER",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp,
                    fontSize = 13.sp,
                )
            }
        } else {
            OutlinedButton(
                onClick = onAnnuler,
                modifier = Modifier
                    .height(38.dp)
                    .semantics { contentDescription = "Annuler la dernière action" },
                shape = FormeBouton,
            ) {
                Text("↩ Annuler", fontSize = 12.sp)
            }

            OutlinedButton(
                onClick = onRefaire,
                modifier = Modifier
                    .height(38.dp)
                    .semantics { contentDescription = "Rétablir l'action annulée" },
                shape = FormeBouton,
            ) {
                Text("↪ Refaire", fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.weight(1f))

            BoutonFinDuTour(
                onClick = { confirmerFinDuTour = true },
                modifier = Modifier.height(38.dp),
                compact = false,
            )
        }
    }
}

@Composable
private fun BoutonFinDuTour(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean,
) {
    Box(
        modifier = modifier
            .clip(FormeBouton)
            .background(MaterialTheme.colorScheme.primary)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Fin du tour",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = if (compact) 11.sp else 13.sp,
            modifier = Modifier.padding(horizontal = if (compact) 10.dp else 18.dp),
        )
    }
}

@Composable
private fun BoutonContourCompact(
    texte: String,
    description: String,
    onClick: () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .height(40.dp)
            .semantics { contentDescription = description },
        shape = FormeBouton,
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
    ) {
        Text(texte, fontSize = 14.sp)
    }
}
