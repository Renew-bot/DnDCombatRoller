package com.example.dndcombatroller.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dndcombatroller.domain.model.ResultatJet
import com.example.dndcombatroller.domain.model.TypeAvantage

@Composable
fun Historique(
    entrees: List<EntreeHistorique>,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(entrees.size) {
        if (entrees.isNotEmpty()) {
            listState.animateScrollToItem(entrees.size - 1)
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.padding(horizontal = 8.dp),
        reverseLayout = true,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        if (entrees.isEmpty()) {
            item {
                Text(
                    text = "Aucun jet effectué",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }
        }

        items(entrees) { entree ->
            EntreeHistoriqueItem(entree)
        }
    }
}

@Composable
private fun EntreeHistoriqueItem(entree: EntreeHistorique) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${entree.nomAttaque}  ·  ${entree.libelleEtape}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (entree.resultat != null) {
                    Text(
                        text = buildDetail(entree.resultat),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                    )
                } else if (entree.texte != null) {
                    val texteAffiche = if (entree.texte.length > 50) entree.texte.take(47) + "..." else entree.texte
                    Text(
                        text = texteAffiche,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                    )
                }
            }
            if (entree.resultat != null) {
                Text(
                    text = "→ ${entree.resultat.total}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}

private fun buildDetail(result: ResultatJet): String = buildString {
    when {
        result.avantage != TypeAvantage.NORMAL && result.desTires.size == 2 -> {
            val (d1, d2) = result.desTires
            val garde = if (result.avantage == TypeAvantage.AVANTAGE) maxOf(d1, d2) else minOf(d1, d2)
            val ecarte = if (garde == d1) d2 else d1
            append("[$ecarte → $garde]")  // barré non disponible en String pur
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
