package com.example.dndcombatroller.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dndcombatroller.domain.model.ResultatJet
import com.example.dndcombatroller.domain.model.TypeAvantage

@Composable
fun BarreHaut(
    nomPersonnage: String,
    dernierResultat: ResultatJet?,
    onNomPersonnageChange: (String) -> Unit,
    onOuvrirFiche: () -> Unit,
    modePortrait: Boolean = false,
    degatsDuTour: Int = 0,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        // Encart Résultat (gauche, animé)
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
        ) {
            AnimatedContent(
                targetState = dernierResultat,
                transitionSpec = {
                    (slideInVertically(tween(260)) { it } + fadeIn(tween(200))) togetherWith
                        (slideOutVertically(tween(200)) { -it } + fadeOut(tween(150)))
                },
                label = "résultat",
            ) { result ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    if (result == null) {
                        Text(
                            text = "—",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${result.total}",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.secondary,
                            )
                            DetailResultat(result)
                        }
                    }
                }
            }
        }

        if (modePortrait) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "$degatsDuTour",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.tertiary,
                    lineHeight = 40.sp,
                )
                Text(
                    text = "dmg / tour",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                NomEditable(
                    valeur = nomPersonnage,
                    onChange = onNomPersonnageChange,
                )
                OutlinedButton(
                    onClick = onOuvrirFiche,
                    modifier = Modifier
                        .height(32.dp)
                        .semantics { contentDescription = "Ouvrir la fiche personnage" },
                ) {
                    Text("Fiche", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
private fun NomEditable(
    valeur: String,
    onChange: (String) -> Unit,
) {
    val couleurTexte = MaterialTheme.colorScheme.onSurface
    val couleurPlaceholder = MaterialTheme.colorScheme.onSurfaceVariant
    val couleurCurseur = MaterialTheme.colorScheme.primary
    val styleTexte = MaterialTheme.typography.titleSmall.copy(color = couleurTexte)

    BasicTextField(
        value = valeur,
        onValueChange = onChange,
        textStyle = styleTexte,
        cursorBrush = SolidColor(couleurCurseur),
        singleLine = true,
        modifier = Modifier.widthIn(min = 60.dp, max = 200.dp),
        decorationBox = { innerTextField ->
            Box {
                if (valeur.isBlank()) {
                    Text(
                        text = "Personnage",
                        style = MaterialTheme.typography.titleSmall,
                        color = couleurPlaceholder,
                    )
                }
                innerTextField()
            }
        },
    )
}

@Composable
private fun DetailResultat(result: ResultatJet) {
    val mutedColor = MaterialTheme.colorScheme.onSurfaceVariant
    val onSurface = MaterialTheme.colorScheme.onSurface

    val texte = buildAnnotatedString {
        when {
            result.avantage != TypeAvantage.NORMAL && result.desTires.size == 2 -> {
                val (d1, d2) = result.desTires
                val garde = if (result.avantage == TypeAvantage.AVANTAGE) maxOf(d1, d2) else minOf(d1, d2)
                val ecarte = if (garde == d1) d2 else d1
                withStyle(SpanStyle(textDecoration = TextDecoration.LineThrough, color = mutedColor)) {
                    append("$ecarte")
                }
                append("  ")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = onSurface)) {
                    append("$garde")
                }
            }
            else -> append(result.desTires.joinToString("  "))
        }
        if (result.modificateurExpression != 0) {
            val s = if (result.modificateurExpression > 0) "+${result.modificateurExpression}" else "${result.modificateurExpression}"
            append("  $s")
        }
        if (result.modificateurFlat != 0) {
            val s = if (result.modificateurFlat > 0) "+${result.modificateurFlat}" else "${result.modificateurFlat}"
            append("  $s")
        }
    }

    Text(
        text = texte,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}
