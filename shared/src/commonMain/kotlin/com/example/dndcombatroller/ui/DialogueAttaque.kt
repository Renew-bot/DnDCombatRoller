package com.example.dndcombatroller.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.dndcombatroller.domain.engine.ExpressionParseur
import com.example.dndcombatroller.domain.engine.ResultatParsage
import com.example.dndcombatroller.domain.model.Attaque
import com.example.dndcombatroller.domain.model.EtapeDeJet
import com.example.dndcombatroller.domain.model.TypeJet
import com.example.dndcombatroller.ui.theme.FormeBouton
import com.example.dndcombatroller.ui.theme.FormeCarte

private data class EtapeBrouillon(
    val libelle: String = "",
    val expression: String = "",
    val type: TypeJet = TypeJet.ATTAQUE,
)

private fun TypeJet.label(): String = when (this) {
    TypeJet.ATTAQUE -> "Attaque"
    TypeJet.DEGATS -> "Dégâts"
    TypeJet.AUTRE -> "Autre"
}

private fun expressionValide(expr: String): Boolean =
    ExpressionParseur.parse(expr.trim()) is ResultatParsage.Succes

@Composable
fun DialogueAttaque(
    attaque: Attaque? = null,
    onEnregistrer: (nom: String, etapes: List<EtapeDeJet>) -> Unit,
    onDismiss: () -> Unit,
) {
    var nom by remember(attaque) { mutableStateOf(attaque?.nom ?: "") }
    var nomErreur by remember { mutableStateOf(false) }
    val etapes: SnapshotStateList<EtapeBrouillon> = remember(attaque) {
        (attaque?.etapes?.map { EtapeBrouillon(it.libelle, it.expression, it.type) }
            ?: listOf(EtapeBrouillon())).toMutableStateList()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .fillMaxHeight(0.9f),
            shape = FormeCarte,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp,
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (attaque == null) "Nouvelle attaque" else "Modifier l'attaque",
                    style = MaterialTheme.typography.titleLarge,
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Contenu scrollable
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OutlinedTextField(
                        value = nom,
                        onValueChange = { nom = it; nomErreur = false },
                        label = { Text("Nom de l'attaque") },
                        isError = nomErreur,
                        supportingText = if (nomErreur) {
                            { Text("Le nom ne peut pas être vide") }
                        } else null,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Text(
                        text = "Étapes",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    etapes.forEachIndexed { index, etape ->
                        EtapeItem(
                            etape = etape,
                            numero = index + 1,
                            peutSupprimer = etapes.size > 1,
                            onUpdate = { etapes[index] = it },
                            onSupprimer = { etapes.removeAt(index) },
                        )
                    }

                    TextButton(
                        onClick = { etapes.add(EtapeBrouillon()) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        shape = FormeBouton,
                    ) {
                        Text("+ Ajouter une étape")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Boutons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.height(36.dp),
                        shape = FormeBouton,
                    ) {
                        Text("Annuler")
                    }
                    Button(
                        shape = FormeBouton,
                        onClick = {
                            nomErreur = nom.isBlank()
                            val expressionsOk = etapes.all {
                                it.expression.isNotBlank() &&
                                    (it.type == TypeJet.AUTRE || expressionValide(it.expression))
                            }
                            if (!nomErreur && expressionsOk) {
                                onEnregistrer(
                                    nom.trim(),
                                    etapes.map {
                                        EtapeDeJet(
                                            libelle = it.libelle.trim(),
                                            expression = it.expression.trim(),
                                            type = it.type,
                                        )
                                    },
                                )
                            }
                        },
                        modifier = Modifier.height(36.dp),
                    ) {
                        Text("Enregistrer")
                    }
                }
            }
        }
    }
}

@Composable
private fun EtapeItem(
    etape: EtapeBrouillon,
    numero: Int,
    peutSupprimer: Boolean,
    onUpdate: (EtapeBrouillon) -> Unit,
    onSupprimer: () -> Unit,
) {
    val expressionErronee = etape.type != TypeJet.AUTRE
        && etape.expression.isNotBlank()
        && !expressionValide(etape.expression)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, FormeBouton),
        shape = FormeBouton,
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Étape $numero",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (peutSupprimer) {
                    TextButton(
                        onClick = onSupprimer,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error,
                        ),
                    ) {
                        Text("✕ Supprimer")
                    }
                }
            }

            OutlinedTextField(
                value = etape.libelle,
                onValueChange = { onUpdate(etape.copy(libelle = it)) },
                label = { Text("Libellé  (ex : Jet d'attaque)") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            )

            if (etape.type == TypeJet.AUTRE) {
                OutlinedTextField(
                    value = etape.expression,
                    onValueChange = { onUpdate(etape.copy(expression = it)) },
                    label = { Text("Texte libre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                OutlinedTextField(
                    value = etape.expression,
                    onValueChange = { onUpdate(etape.copy(expression = it)) },
                    label = { Text("Dés  (ex : 1d20+4)") },
                    isError = expressionErronee,
                    supportingText = if (expressionErronee) {
                        { Text("Format : 1d20+5, 2d4+1d6, 4d10+1d8+4") }
                    } else null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                TypeJet.entries.forEach { type ->
                    BoutonBascule(
                        texte = type.label(),
                        selectionne = etape.type == type,
                        onClick = { onUpdate(etape.copy(type = type)) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}
