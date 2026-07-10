package com.example.dndcombatroller.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val FormePilule = RoundedCornerShape(50.dp)
private val GradientFinDuTour = Brush.horizontalGradient(listOf(Color(0xFFFF7750), Color(0xFFFF78FF)))

@Composable
fun BarreBas(
    degatsDuTour: Int,
    onAnnuler: () -> Unit,
    onRefaire: () -> Unit,
    onFinDuTour: () -> Unit,
    modifier: Modifier = Modifier,
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
                Button(onClick = { onFinDuTour(); confirmerFinDuTour = false }) {
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
            .height(52.dp)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OutlinedButton(
            onClick = onAnnuler,
            modifier = Modifier
                .height(38.dp)
                .semantics { contentDescription = "Annuler la dernière action" },
            shape = FormePilule,
        ) {
            Text("↩ Annuler", fontSize = 12.sp)
        }

        OutlinedButton(
            onClick = onRefaire,
            modifier = Modifier
                .height(38.dp)
                .semantics { contentDescription = "Rétablir l'action annulée" },
            shape = FormePilule,
        ) {
            Text("↪ Refaire", fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .height(38.dp)
                .shadow(6.dp, FormePilule)
                .clip(FormePilule)
                .background(GradientFinDuTour)
                .clickable { confirmerFinDuTour = true },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Fin du tour",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 18.dp),
            )
        }
    }
}
