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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicTextField
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
import com.example.dndcombatroller.ui.theme.FormeBouton
import com.example.dndcombatroller.ui.theme.FormeCarte
import com.example.dndcombatroller.ui.theme.OrNatural20Fond
import com.example.dndcombatroller.ui.theme.OrNatural20Texte
import com.example.dndcombatroller.ui.theme.RougeNatural1Fond
import com.example.dndcombatroller.ui.theme.RougeNatural1Texte

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
    if (modePortrait) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NomEditable(
                    valeur = nomPersonnage,
                    onChange = onNomPersonnageChange,
                )
                OutlinedButton(
                    onClick = onOuvrirFiche,
                    modifier = Modifier
                        .height(30.dp)
                        .semantics { contentDescription = "Ouvrir la fiche personnage" },
                    shape = FormeBouton,
                ) {
                    Text("Fiche", style = MaterialTheme.typography.labelSmall)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().height(72.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CarteResultat(
                    dernierResultat = dernierResultat,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                )
                CarteDegats(
                    degatsDuTour = degatsDuTour,
                    modifier = Modifier.width(64.dp).fillMaxHeight(),
                )
            }
        }
    } else {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(96.dp)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            CarteResultat(
                dernierResultat = dernierResultat,
                modifier = Modifier.weight(1f).fillMaxHeight().padding(end = 12.dp),
            )

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
                    shape = FormeBouton,
                ) {
                    Text("Fiche", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
private fun CarteResultat(
    dernierResultat: ResultatJet?,
    modifier: Modifier = Modifier,
) {
    val natural20 = dernierResultat?.estNatural20 == true
    val natural1 = dernierResultat?.estNatural1 == true
    Card(
        modifier = modifier,
        shape = FormeCarte,
        colors = CardDefaults.cardColors(
            containerColor = when {
                natural20 -> OrNatural20Fond
                natural1 -> RougeNatural1Fond
                else -> MaterialTheme.colorScheme.primaryContainer
            },
        ),
    ) {
        AnimatedContent(
            targetState = dernierResultat,
            transitionSpec = {
                (slideInVertically(tween(260)) { it } + fadeIn(tween(200))) togetherWith
                    (slideOutVertically(tween(200)) { -it } + fadeOut(tween(150)))
            },
            label = "résultat",
            modifier = Modifier.fillMaxSize(),
        ) { result ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (result == null) {
                    Text(
                        text = "—",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else if (result.estNatural20) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "NATURAL 20 !",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = OrNatural20Texte,
                        )
                        Text(
                            text = "${result.total}",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = OrNatural20Texte,
                        )
                    }
                } else if (result.estNatural1) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "NATURAL 1",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = RougeNatural1Texte,
                        )
                        Text(
                            text = "${result.total}",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = RougeNatural1Texte,
                        )
                    }
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
}

@Composable
private fun CarteDegats(
    degatsDuTour: Int,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = FormeCarte,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "$degatsDuTour",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.tertiary,
                    lineHeight = 28.sp,
                )
                Text(
                    text = "dmg/tour",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
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
