package com.example.dndcombatroller.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dndcombatroller.data.LocalSelecteurFichier
import com.example.dndcombatroller.domain.model.Caracteristique
import com.example.dndcombatroller.domain.model.FichePersonnage
import com.example.dndcombatroller.domain.model.LigneMaitrise
import com.example.dndcombatroller.ui.theme.DnDCombatRollerTheme
import com.example.dndcombatroller.ui.theme.FormeBouton
import com.example.dndcombatroller.ui.theme.FormeCarte

@Composable
fun FichePersoScreen(
    fiche: FichePersonnage?,
    pvActuel: Int,
    pvTemporaires: Int,
    onImporterFiche: (String) -> Unit,
    onAjusterPvActuel: (Int) -> Unit,
    onAjusterPvTemporaires: (Int) -> Unit,
    onRetour: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val selecteurFichier = LocalSelecteurFichier.current

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(Modifier.fillMaxSize().safeDrawingPadding()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedButton(
                    onClick = onRetour,
                    shape = FormeBouton,
                    modifier = Modifier.semantics { contentDescription = "Retour au combat" },
                ) {
                    Text("← Combat", style = MaterialTheme.typography.labelSmall)
                }

                Column(
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = fiche?.nom ?: "Aucune fiche",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (fiche != null) {
                        Text(
                            text = "${fiche.classeNiveau} · ${fiche.race}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }

                OutlinedButton(
                    onClick = { selecteurFichier.ouvrir(onImporterFiche) },
                    shape = FormeBouton,
                    modifier = Modifier.semantics { contentDescription = "Importer une fiche de personnage" },
                ) {
                    Text("Importer", style = MaterialTheme.typography.labelSmall)
                }
            }

            HorizontalDivider()

            if (fiche == null) {
                Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = "Aucune fiche importée",
                            style = MaterialTheme.typography.titleSmall,
                        )
                        Text(
                            text = "Exportez votre personnage depuis aidedd.org (feuille imprimable) et importez le fichier HTML avec le bouton « Importer ».",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    GrilleValeurs(
                        listOf(
                            "CA" to fiche.ca,
                            "Initiative" to fiche.initiative,
                            "Vitesse" to fiche.vitesse,
                            "PV max" to fiche.pvMax,
                            "Perception pass." to fiche.perceptionPassive,
                        ),
                    )

                    BarrePv(
                        pvActuel = pvActuel,
                        pvMax = fiche.pvMax.toIntOrNull(),
                        pvTemporaires = pvTemporaires,
                        onAjusterPvActuel = onAjusterPvActuel,
                        onAjusterPvTemporaires = onAjusterPvTemporaires,
                    )

                    SectionFiche(titre = "Caractéristiques") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            fiche.caracteristiques.forEach { c -> CarteCaracteristique(c, Modifier.weight(1f)) }
                        }
                    }

                    SectionFiche(titre = "Jets de sauvegarde") {
                        GrilleMaitrises(lignes = fiche.sauvegardes, colonnes = 2)
                    }

                    SectionFiche(titre = "Compétences") {
                        GrilleMaitrises(lignes = fiche.competences, colonnes = 2)
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionFiche(titre: String, contenu: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = titre.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        contenu()
    }
}

@Composable
private fun BarrePv(
    pvActuel: Int,
    pvMax: Int?,
    pvTemporaires: Int,
    onAjusterPvActuel: (Int) -> Unit,
    onAjusterPvTemporaires: (Int) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = FormeCarte,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "PV",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = if (pvMax != null) "$pvActuel / $pvMax" else "$pvActuel",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    if (pvTemporaires > 0) {
                        Text(
                            text = "+$pvTemporaires PVT",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                        )
                    }
                }
            }

            BarreProgressionPv(pvActuel = pvActuel, pvMax = pvMax, pvTemporaires = pvTemporaires)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ControlePv(
                    label = "PV",
                    couleur = MaterialTheme.colorScheme.primary,
                    onMoins = { montant -> onAjusterPvActuel(-montant) },
                    onPlus = { montant -> onAjusterPvActuel(montant) },
                    modifier = Modifier.weight(1f),
                )
                ControlePv(
                    label = "PVT",
                    couleur = MaterialTheme.colorScheme.secondary,
                    onMoins = { montant -> onAjusterPvTemporaires(-montant) },
                    onPlus = { montant -> onAjusterPvTemporaires(montant) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun BarreProgressionPv(pvActuel: Int, pvMax: Int?, pvTemporaires: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(MaterialTheme.colorScheme.background),
    ) {
        if (pvMax != null && pvMax > 0) {
            // L'échelle inclut les PV temporaires pour qu'ils restent visibles même à PV pleins.
            val total = pvMax + pvTemporaires
            val fractionPv = (pvActuel.toFloat() / total).coerceIn(0f, 1f)
            val fractionTemp = (pvTemporaires.toFloat() / total).coerceIn(0f, 1f - fractionPv)
            val reste = (1f - fractionPv - fractionTemp).coerceAtLeast(0f)
            Row(Modifier.fillMaxSize()) {
                if (fractionPv > 0f) {
                    Box(Modifier.weight(fractionPv).fillMaxHeight().background(MaterialTheme.colorScheme.primary))
                }
                if (fractionTemp > 0f) {
                    Box(Modifier.weight(fractionTemp).fillMaxHeight().background(MaterialTheme.colorScheme.secondary))
                }
                if (reste > 0f) {
                    Spacer(Modifier.weight(reste))
                }
            }
        }
    }
}

@Composable
private fun ControlePv(
    label: String,
    couleur: Color,
    onMoins: (montant: Int) -> Unit,
    onPlus: (montant: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Le texte saisi est la source de vérité de l'affichage : il peut donc rester vide
    // pendant la frappe sans être re-rempli de force par la dernière valeur valide.
    var texte by rememberSaveable { mutableStateOf("1") }
    val montant = texte.toIntOrNull()?.coerceIn(1, 99) ?: 1

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = couleur,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        FilledTonalButton(
            onClick = { onMoins(montant) },
            modifier = Modifier.size(28.dp).semantics { contentDescription = "Retirer $montant $label" },
            contentPadding = PaddingValues(0.dp),
        ) {
            Text("–", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        ChampDelta(texte = texte, onTexteChange = { texte = it }, couleur = couleur)
        FilledTonalButton(
            onClick = { onPlus(montant) },
            modifier = Modifier.size(28.dp).semantics { contentDescription = "Ajouter $montant $label" },
            contentPadding = PaddingValues(0.dp),
        ) {
            Text("+", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ChampDelta(texte: String, onTexteChange: (String) -> Unit, couleur: Color) {
    BasicTextField(
        value = texte,
        onValueChange = { saisie ->
            val filtre = saisie.filter { it.isDigit() }
            if (filtre.length <= 2) onTexteChange(filtre)
        },
        textStyle = MaterialTheme.typography.labelSmall.copy(
            color = couleur,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.widthIn(min = 18.dp, max = 28.dp),
    )
}

private val HauteurCase = 64.dp

private val AbreviationsCaracteristiques = mapOf(
    "Force" to "For.",
    "Dextérité" to "Dex.",
    "Constitution" to "Const.",
    "Intelligence" to "Int.",
    "Sagesse" to "Sag.",
    "Charisme" to "Cha.",
)

private fun abrevier(nom: String): String = AbreviationsCaracteristiques[nom] ?: nom

@Composable
private fun GrilleValeurs(valeurs: List<Pair<String, String>>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        valeurs.forEach { (label, valeur) ->
            Card(
                modifier = Modifier.weight(1f).height(HauteurCase),
                shape = FormeCarte,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = valeur,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun CarteCaracteristique(caracteristique: Caracteristique, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(HauteurCase),
        shape = FormeCarte,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = abrevier(caracteristique.nom),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = caracteristique.mod,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = caracteristique.score,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun GrilleMaitrises(lignes: List<LigneMaitrise>, colonnes: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        lignes.chunked(colonnes).forEach { rangee ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                rangee.forEach { ligne -> LigneMaitriseItem(ligne, Modifier.weight(1f)) }
                repeat(colonnes - rangee.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun LigneMaitriseItem(ligne: LigneMaitrise, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(vertical = 1.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(if (ligne.maitrise) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.outline),
        )
        Text(
            text = ligne.nom,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = ligne.mod,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Preview(
    name = "Fiche personnage",
    widthDp = 760,
    heightDp = 340,
    showBackground = true,
)
@Composable
private fun FichePersoScreenPreview() {
    val fiche = FichePersonnage(
        nom = "Lupus",
        classeNiveau = "Moine 4",
        race = "Féral (h)",
        ca = "16",
        initiative = "+3",
        vitesse = "12 m",
        pvMax = "19",
        perceptionPassive = "12",
        caracteristiques = listOf(
            Caracteristique("Force", "+0", "10"),
            Caracteristique("Dextérité", "+3", "17"),
            Caracteristique("Constitution", "+3", "16"),
            Caracteristique("Intelligence", "+0", "10"),
            Caracteristique("Sagesse", "+2", "15"),
            Caracteristique("Charisme", "+0", "10"),
        ),
        sauvegardes = listOf(
            LigneMaitrise("Force", "+2", true),
            LigneMaitrise("Dextérité", "+6", true),
            LigneMaitrise("Constitution", "+3", false),
            LigneMaitrise("Intelligence", "+0", false),
            LigneMaitrise("Sagesse", "+2", false),
            LigneMaitrise("Charisme", "+0", false),
        ),
        competences = listOf(
            LigneMaitrise("Acrobaties (Dex)", "+5", true),
            LigneMaitrise("Perception (Sag)", "+4", true),
            LigneMaitrise("Survie (Sag)", "+4", true),
        ),
    )
    DnDCombatRollerTheme {
        FichePersoScreen(
            fiche = fiche,
            pvActuel = 14,
            pvTemporaires = 3,
            onImporterFiche = {},
            onAjusterPvActuel = {},
            onAjusterPvTemporaires = {},
            onRetour = {},
        )
    }
}
