package com.example.dndcombatroller.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
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

private val FormeAttaque = RoundedCornerShape(18.dp)
private val FormePilule = RoundedCornerShape(50.dp)

private val GradientLancer = Brush.horizontalGradient(listOf(Color(0xFF5032FF), Color(0xFFFF78FF)))
private val GradientLancerOff = Brush.horizontalGradient(listOf(Color(0xFF001040), Color(0xFF001040)))
private val GradientSelectionne = Brush.horizontalGradient(listOf(Color(0xFF5032FF), Color(0xFF7A5AFF)))
private val GradientCarte = Brush.horizontalGradient(listOf(Color(0xFF001455), Color(0xFF0A2480)))

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
fun PanneauAttaques(
    attaques: List<Attaque>,
    idAttaqueSelectionnee: String?,
    onSelectionnerAttaque: (String) -> Unit,
    onAjouterAttaque: (nom: String, etapes: List<EtapeDeJet>) -> Unit,
    onModifierAttaque: (Attaque) -> Unit,
    onSupprimerAttaque: (id: String) -> Unit,
    onDeplacerAttaque: (de: Int, vers: Int) -> Unit,
    onLancer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var dialogueOuvert by remember { mutableStateOf(false) }
    var attaqueEnEdition by remember { mutableStateOf<Attaque?>(null) }
    var menuOuvertPourId by remember { mutableStateOf<String?>(null) }

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

    Surface(
        modifier = modifier,
        tonalElevation = 2.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 10.dp),
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
                shape = FormePilule,
            ) {
                Text(
                    text = "+ AJOUTER",
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    fontSize = 12.sp,
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
                        menuOuvert = menuOuvertPourId == attaque.id,
                        onSelectionner = { onSelectionnerAttaque(attaque.id) },
                        onOuvrirMenu = { menuOuvertPourId = attaque.id },
                        onFermerMenu = { menuOuvertPourId = null },
                        onModifier = {
                            attaqueEnEdition = attaque
                            dialogueOuvert = true
                            menuOuvertPourId = null
                        },
                        onSupprimer = {
                            onSupprimerAttaque(attaque.id)
                            menuOuvertPourId = null
                        },
                        modifier = Modifier
                            .zIndex(if (enDrag) 1f else 0f)
                            .graphicsLayer {
                                translationY = if (enDrag) dragDrop.decalageVisuel else 0f
                                alpha = if (enDrag) 0.85f else 1f
                                shadowElevation = if (enDrag) 16f else 0f
                            },
                    )
                }
            }

            val actif = idAttaqueSelectionnee != null
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .shadow(if (actif) 12.dp else 0.dp, FormePilule)
                    .clip(FormePilule)
                    .background(if (actif) GradientLancer else GradientLancerOff)
                    .clickable(enabled = actif, onClick = onLancer),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "LANCER",
                    color = Color.White.copy(alpha = if (actif) 1f else 0.3f),
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp,
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
    menuOuvert: Boolean,
    onSelectionner: () -> Unit,
    onOuvrirMenu: () -> Unit,
    onFermerMenu: () -> Unit,
    onModifier: () -> Unit,
    onSupprimer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(if (selectionnee) 8.dp else 2.dp, FormeAttaque)
                .clip(FormeAttaque)
                .background(if (selectionnee) GradientSelectionne else GradientCarte)
                .semantics { selected = selectionnee }
                .clickable(onClick = onSelectionner),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = attaque.nom,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 14.dp, top = 12.dp, bottom = 12.dp),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (selectionnee) FontWeight.Bold else FontWeight.Normal,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            IconButton(onClick = onOuvrirMenu) {
                Text(
                    text = "⋮",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White.copy(alpha = 0.7f),
                )
            }
        }

        DropdownMenu(
            expanded = menuOuvert,
            onDismissRequest = onFermerMenu,
        ) {
            DropdownMenuItem(
                text = { Text("Modifier") },
                onClick = onModifier,
            )
            DropdownMenuItem(
                text = {
                    Text(
                        text = "Supprimer",
                        color = MaterialTheme.colorScheme.error,
                    )
                },
                onClick = onSupprimer,
            )
        }
    }
}
