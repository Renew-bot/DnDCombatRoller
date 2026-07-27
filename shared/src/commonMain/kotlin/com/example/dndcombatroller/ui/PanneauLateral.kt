package com.example.dndcombatroller.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.dndcombatroller.domain.model.Attaque
import com.example.dndcombatroller.domain.model.EtapeDeJet
import com.example.dndcombatroller.ui.theme.FormeBouton

private enum class OngletLateral { ATTAQUES, HISTORIQUE }

private class DragDropEtat(
    private val listState: LazyListState,
    private val onDeplacer: (de: Int, vers: Int) -> Unit,
) {
    var indexEnDrag by mutableStateOf<Int?>(null)
        private set
    private var decalageBrut by mutableStateOf(0f)
    private var decalageInitial = 0

    val decalageVisuel: Float
        get() {
            val info = listState.layoutInfo.visibleItemsInfo
                .firstOrNull { it.index == indexEnDrag } ?: return 0f
            return decalageInitial + decalageBrut - info.offset
        }

    fun demarrer(offset: Offset) {
        listState.layoutInfo.visibleItemsInfo
            .firstOrNull { offset.y.toInt() in it.offset..(it.offset + it.size) }
            ?.let {
                indexEnDrag = it.index
                decalageInitial = it.offset
                decalageBrut = 0f
            }
    }

    fun onDrag(delta: Offset) {
        decalageBrut += delta.y
        val infoActuelle = listState.layoutInfo.visibleItemsInfo
            .firstOrNull { it.index == indexEnDrag } ?: return
        val centre = (decalageInitial + decalageBrut + infoActuelle.size / 2f).toInt()

        val cible = listState.layoutInfo.visibleItemsInfo
            .filterNot { it.index == indexEnDrag }
            .firstOrNull { centre in it.offset..(it.offset + it.size) }

        if (cible != null) {
            onDeplacer(indexEnDrag!!, cible.index)
            decalageInitial = cible.offset
            decalageBrut = 0f
            indexEnDrag = cible.index
        }
    }

    fun terminer() {
        indexEnDrag = null
        decalageBrut = 0f
        decalageInitial = 0
    }
}

@Composable
fun PanneauLateral(
    attaques: List<Attaque>,
    idAttaqueSelectionnee: String?,
    historique: List<EntreeHistorique>,
    onSelectionnerAttaque: (String) -> Unit,
    onAjouterAttaque: (nom: String, etapes: List<EtapeDeJet>) -> Unit,
    onModifierAttaque: (Attaque) -> Unit,
    onSupprimerAttaque: (id: String) -> Unit,
    onDeplacerAttaque: (de: Int, vers: Int) -> Unit,
    modifier: Modifier = Modifier,
    afficherBoutonLancer: Boolean = true,
    onLancer: () -> Unit = {},
) {
    var onglet by remember { mutableStateOf(OngletLateral.ATTAQUES) }
    var dialogueOuvert by remember { mutableStateOf(false) }
    var attaqueEnEdition by remember { mutableStateOf<Attaque?>(null) }

    if (dialogueOuvert) {
        DialogueAttaque(
            attaque = attaqueEnEdition,
            onEnregistrer = { nom, etapes ->
                val enEdition = attaqueEnEdition
                if (enEdition == null) {
                    onAjouterAttaque(nom, etapes)
                } else {
                    onModifierAttaque(enEdition.copy(nom = nom, etapes = etapes))
                }
                dialogueOuvert = false
                attaqueEnEdition = null
            },
            onDismiss = {
                dialogueOuvert = false
                attaqueEnEdition = null
            },
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            BoutonBascule(
                texte = "Attaques",
                selectionne = onglet == OngletLateral.ATTAQUES,
                onClick = { onglet = OngletLateral.ATTAQUES },
                modifier = Modifier.weight(1f),
            )
            BoutonBascule(
                texte = "Historique",
                selectionne = onglet == OngletLateral.HISTORIQUE,
                onClick = { onglet = OngletLateral.HISTORIQUE },
                modifier = Modifier.weight(1f),
            )
        }

        when (onglet) {
            OngletLateral.ATTAQUES -> Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedButton(
                    onClick = {
                        attaqueEnEdition = null
                        dialogueOuvert = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    shape = FormeBouton,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                ) {
                    Text(
                        text = "+ Ajouter",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                    )
                }

                val listState = rememberLazyListState()
                val dragDrop = remember(listState) {
                    DragDropEtat(listState) { de, vers -> onDeplacerAttaque(de, vers) }
                }

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .pointerInput(dragDrop) {
                            detectDragGesturesAfterLongPress(
                                onDragStart = { offset -> dragDrop.demarrer(offset) },
                                onDrag = { change, delta ->
                                    change.consume()
                                    dragDrop.onDrag(delta)
                                },
                                onDragEnd = { dragDrop.terminer() },
                                onDragCancel = { dragDrop.terminer() },
                            )
                        },
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    items(attaques, key = { it.id }) { attaque ->
                        val index = attaques.indexOf(attaque)
                        val enDrag = dragDrop.indexEnDrag == index
                        AttaqueItem(
                            attaque = attaque,
                            selectionnee = attaque.id == idAttaqueSelectionnee,
                            onSelectionner = { onSelectionnerAttaque(attaque.id) },
                            onModifier = {
                                attaqueEnEdition = attaque
                                dialogueOuvert = true
                            },
                            onSupprimer = { onSupprimerAttaque(attaque.id) },
                            modifier = Modifier
                                .zIndex(if (enDrag) 1f else 0f)
                                .graphicsLayer {
                                    translationY = if (enDrag) dragDrop.decalageVisuel else 0f
                                    alpha = if (enDrag) 0.85f else 1f
                                },
                        )
                    }
                }
            }

            OngletLateral.HISTORIQUE -> Historique(
                entrees = historique,
                modifier = Modifier.weight(1f),
            )
        }

        if (afficherBoutonLancer) {
            val actif = idAttaqueSelectionnee != null
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .alpha(if (actif) 1f else 0.35f)
                    .clip(FormeBouton)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable(enabled = actif, onClick = onLancer),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "LANCER",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp,
                    fontSize = 14.sp,
                )
            }
        }
    }
}

@Composable
private fun AttaqueItem(
    attaque: Attaque,
    selectionnee: Boolean,
    onSelectionner: () -> Unit,
    onModifier: () -> Unit,
    onSupprimer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(FormeBouton)
            .background(if (selectionnee) MaterialTheme.colorScheme.primary else Color.White)
            .border(
                width = if (selectionnee) 0.dp else 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = FormeBouton,
            )
            .semantics { selected = selectionnee }
            .clickable(onClick = onSelectionner)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = attaque.nom,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (selectionnee) FontWeight.Bold else FontWeight.Normal,
            color = if (selectionnee) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (selectionnee) {
            Spacer(Modifier.width(6.dp))
            IconeGlyphe(glyphe = "✎", onClick = onModifier)
            Spacer(Modifier.width(4.dp))
            IconeGlyphe(glyphe = "✕", onClick = onSupprimer)
        }
    }
}

@Composable
private fun IconeGlyphe(glyphe: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(22.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(Color.White.copy(alpha = 0.18f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(glyphe, color = Color.White, fontSize = 11.sp)
    }
}
