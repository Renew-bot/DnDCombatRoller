@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.dndcombatroller.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import com.example.dndcombatroller.domain.model.TypeAvantage
import com.example.dndcombatroller.domain.model.TypeJet

private fun TypeAvantage.label() = when (this) {
    TypeAvantage.AVANTAGE -> "Avantage"
    TypeAvantage.NORMAL -> "Normal"
    TypeAvantage.DESAVANTAGE -> "Désav."
}

private val ORDRE_AVANTAGE = listOf(TypeAvantage.AVANTAGE, TypeAvantage.NORMAL, TypeAvantage.DESAVANTAGE)

private data class EtapeVisuelle(
    val expression: String?,
    val type: TypeJet?,
    val libelle: String?,
)

@Composable
fun ZoneDes(
    libelleEtapeCourante: String?,
    expressionCourante: String?,
    typeEtapeCourante: TypeJet?,
    modificateurFlat: Int,
    avantage: TypeAvantage,
    estD20Unique: Boolean,
    prochainsDes: String?,
    prochainEstTexte: Boolean,
    degatsDuTour: Int,
    onIncrementerModificateurFlat: (Int) -> Unit,
    onDefinirAvantage: (TypeAvantage) -> Unit,
    modePortrait: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        CarteDes(
            libelleEtapeCourante = libelleEtapeCourante,
            expressionCourante = expressionCourante,
            typeEtapeCourante = typeEtapeCourante,
            modificateurFlat = modificateurFlat,
            avantage = avantage,
            estD20Unique = estD20Unique,
            onIncrementerModificateurFlat = onIncrementerModificateurFlat,
            onDefinirAvantage = onDefinirAvantage,
            modePortrait = modePortrait,
            modifier = Modifier
                .weight(2f)
                .fillMaxHeight(),
        )

        Text(
            text = "→",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.outline,
        )

        CarteProchainsDes(
            prochainsDes = prochainsDes,
            estTexte = prochainEstTexte,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
        )

        if (!modePortrait) {
            CompteurDegats(
                degatsDuTour = degatsDuTour,
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight(),
            )
        }
    }
}

@Composable
private fun CarteDes(
    libelleEtapeCourante: String?,
    expressionCourante: String?,
    typeEtapeCourante: TypeJet?,
    modificateurFlat: Int,
    avantage: TypeAvantage,
    estD20Unique: Boolean,
    onIncrementerModificateurFlat: (Int) -> Unit,
    onDefinirAvantage: (TypeAvantage) -> Unit,
    modePortrait: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
    ) {
        AnimatedContent(
            targetState = EtapeVisuelle(expressionCourante, typeEtapeCourante, libelleEtapeCourante),
            transitionSpec = {
                (slideInHorizontally(tween(260)) { it } + fadeIn(tween(260))) togetherWith
                    (slideOutVertically(tween(200)) { -it } + fadeOut(tween(160)))
            },
            label = "carte-des",
            modifier = Modifier.fillMaxSize(),
        ) { etape ->
            if (etape.expression == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Sélectionnez une action",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f),
                    )
                }
            } else if (etape.type == TypeJet.AUTRE) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = etape.libelle ?: "—",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                    )
                    Box(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = etape.expression,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = etape.libelle ?: "—",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        val texte = buildString {
                            append(etape.expression)
                            if (modificateurFlat != 0) {
                                append(" (")
                                if (modificateurFlat > 0) append("+")
                                append(modificateurFlat)
                                append(")")
                            }
                        }
                        Text(
                            text = texte,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            textAlign = TextAlign.Center,
                        )
                    }

                    if (modePortrait) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            ControleModificateurFlat(
                                valeur = modificateurFlat,
                                onIncrementer = onIncrementerModificateurFlat,
                            )
                            ControleAvantage(
                                avantage = avantage,
                                actif = estD20Unique,
                                onSelectionner = onDefinirAvantage,
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            ControleModificateurFlat(
                                valeur = modificateurFlat,
                                onIncrementer = onIncrementerModificateurFlat,
                            )
                            ControleAvantage(
                                avantage = avantage,
                                actif = estD20Unique,
                                onSelectionner = onDefinirAvantage,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ControleModificateurFlat(
    valeur: Int,
    onIncrementer: (Int) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        FilledTonalButton(
            onClick = { onIncrementer(-1) },
            modifier = Modifier.size(36.dp).semantics { contentDescription = "Diminuer le modificateur" },
            contentPadding = PaddingValues(0.dp),
        ) {
            Text("–", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Text(
            text = if (valeur > 0) "+$valeur" else "$valeur",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(min = 32.dp),
            color = MaterialTheme.colorScheme.onSecondaryContainer,
        )

        FilledTonalButton(
            onClick = { onIncrementer(+1) },
            modifier = Modifier.size(36.dp).semantics { contentDescription = "Augmenter le modificateur" },
            contentPadding = PaddingValues(0.dp),
        ) {
            Text("+", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ControleAvantage(
    avantage: TypeAvantage,
    actif: Boolean,
    onSelectionner: (TypeAvantage) -> Unit,
) {
    SingleChoiceSegmentedButtonRow {
        ORDRE_AVANTAGE.forEachIndexed { i, type ->
            SegmentedButton(
                selected = avantage == type,
                onClick = { onSelectionner(type) },
                enabled = actif || type == TypeAvantage.NORMAL,
                shape = SegmentedButtonDefaults.itemShape(index = i, count = ORDRE_AVANTAGE.size),
            ) {
                Text(
                    text = type.label(),
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun CarteProchainsDes(
    prochainsDes: String?,
    estTexte: Boolean,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        AnimatedContent(
            targetState = prochainsDes to estTexte,
            transitionSpec = {
                (slideInHorizontally(tween(260)) { it } + fadeIn(tween(260))) togetherWith
                    (slideOutHorizontally(tween(200)) { -it } + fadeOut(tween(160)))
            },
            label = "prochain-des",
            modifier = Modifier.fillMaxSize(),
        ) { (texte, isTexte) ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = if (isTexte) "Prochain" else "Prochains dés",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = texte ?: "—",
                    style = if (isTexte) MaterialTheme.typography.bodySmall else MaterialTheme.typography.titleMedium,
                    color = if (texte != null)
                        MaterialTheme.colorScheme.onSurface
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun CompteurDegats(
    degatsDuTour: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
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
    }
}
