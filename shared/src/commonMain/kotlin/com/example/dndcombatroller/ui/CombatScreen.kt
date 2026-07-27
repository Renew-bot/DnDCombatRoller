package com.example.dndcombatroller.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dndcombatroller.data.LocalCombatRepository
import com.example.dndcombatroller.domain.engine.ExpressionParseur
import com.example.dndcombatroller.domain.engine.ResultatParsage
import com.example.dndcombatroller.domain.model.Attaque
import com.example.dndcombatroller.domain.model.EtapeDeJet
import com.example.dndcombatroller.domain.model.ResultatJet
import com.example.dndcombatroller.domain.model.TypeAvantage
import com.example.dndcombatroller.domain.model.TypeJet
import com.example.dndcombatroller.ui.theme.DnDCombatRollerTheme

@Composable
fun CombatScreen(onOuvrirFiche: () -> Unit = {}) {
    val repository = LocalCombatRepository.current
    val viewModel: CombatViewModel = viewModel { CombatViewModel(repository) }
    val etat by viewModel.uiState.collectAsStateWithLifecycle()
    CombatScreenContent(
        etat = etat,
        onSelectionnerAttaque = viewModel::selectionnerAttaque,
        onAjouterAttaque = { nom, etapes -> viewModel.ajouterAttaque(Attaque.creer(nom, etapes)) },
        onModifierAttaque = viewModel::modifierAttaque,
        onSupprimerAttaque = viewModel::supprimerAttaque,
        onDeplacerAttaque = viewModel::deplacerAttaque,
        onLancer = viewModel::lancer,
        onIncrementerModificateurFlat = viewModel::incrementerModificateurFlat,
        onDefinirAvantage = viewModel::definirAvantage,
        onNomPersonnageChange = viewModel::definirNomPersonnage,
        onAnnuler = viewModel::annuler,
        onRefaire = viewModel::refaire,
        onFinDuTour = viewModel::finDuTour,
        onOuvrirFiche = onOuvrirFiche,
    )
}

@Composable
fun CombatScreenContent(
    etat: EtatCombat,
    onSelectionnerAttaque: (String) -> Unit,
    onAjouterAttaque: (nom: String, etapes: List<EtapeDeJet>) -> Unit,
    onModifierAttaque: (Attaque) -> Unit,
    onSupprimerAttaque: (id: String) -> Unit,
    onDeplacerAttaque: (de: Int, vers: Int) -> Unit,
    onLancer: () -> Unit,
    onIncrementerModificateurFlat: (Int) -> Unit,
    onDefinirAvantage: (TypeAvantage) -> Unit,
    onNomPersonnageChange: (String) -> Unit,
    onAnnuler: () -> Unit,
    onRefaire: () -> Unit,
    onFinDuTour: () -> Unit,
    onOuvrirFiche: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val etapeCourante = etat.attaqueSelectionnee?.etapes?.getOrNull(etat.indexEtapeCourante)
    val typeEtapeCourante = etapeCourante?.type
    val estD20Unique = etapeCourante?.let { etape ->
        when (val p = ExpressionParseur.parse(etape.expression)) {
            is ResultatParsage.Succes -> p.groupes.size == 1 && p.groupes[0].nombre == 1 && p.groupes[0].faces == 20
            is ResultatParsage.Erreur -> false
        }
    } ?: false

    val lancerActif = etat.idAttaqueSelectionnee != null

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
        if (maxWidth < maxHeight) {
            Column(modifier = Modifier.fillMaxSize()) {
                BarreHaut(
                    nomPersonnage = etat.nomPersonnage,
                    dernierResultat = etat.dernierResultat,
                    onNomPersonnageChange = onNomPersonnageChange,
                    onOuvrirFiche = onOuvrirFiche,
                    modePortrait = true,
                    degatsDuTour = etat.degatsDuTour,
                )

                HorizontalDivider()

                ZoneDes(
                    libelleEtapeCourante = etapeCourante?.libelle,
                    expressionCourante = etapeCourante?.expression,
                    typeEtapeCourante = typeEtapeCourante,
                    modificateurFlat = etat.modificateurFlat,
                    avantage = etat.avantage,
                    estD20Unique = estD20Unique,
                    prochainsDes = etat.prochainsDes,
                    prochainEstTexte = etat.prochainEstTexte,
                    degatsDuTour = etat.degatsDuTour,
                    onIncrementerModificateurFlat = onIncrementerModificateurFlat,
                    onDefinirAvantage = onDefinirAvantage,
                    modePortrait = true,
                    afficherProchain = false,
                    modifier = Modifier.height(160.dp),
                )

                PanneauLateral(
                    attaques = etat.attaques,
                    idAttaqueSelectionnee = etat.idAttaqueSelectionnee,
                    historique = etat.historique,
                    onSelectionnerAttaque = onSelectionnerAttaque,
                    onAjouterAttaque = onAjouterAttaque,
                    onModifierAttaque = onModifierAttaque,
                    onSupprimerAttaque = onSupprimerAttaque,
                    onDeplacerAttaque = onDeplacerAttaque,
                    afficherBoutonLancer = false,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                )

                HorizontalDivider()

                BarreBas(
                    degatsDuTour = etat.degatsDuTour,
                    onAnnuler = onAnnuler,
                    onRefaire = onRefaire,
                    onFinDuTour = onFinDuTour,
                    modePortrait = true,
                    onLancer = onLancer,
                    lancerActif = lancerActif,
                )
            }
        } else {
            Row(modifier = Modifier.fillMaxSize()) {
                PanneauLateral(
                    attaques = etat.attaques,
                    idAttaqueSelectionnee = etat.idAttaqueSelectionnee,
                    historique = etat.historique,
                    onSelectionnerAttaque = onSelectionnerAttaque,
                    onAjouterAttaque = onAjouterAttaque,
                    onModifierAttaque = onModifierAttaque,
                    onSupprimerAttaque = onSupprimerAttaque,
                    onDeplacerAttaque = onDeplacerAttaque,
                    afficherBoutonLancer = true,
                    onLancer = onLancer,
                    modifier = Modifier
                        .width(230.dp)
                        .fillMaxHeight(),
                )

                VerticalDivider()

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                ) {
                    BarreHaut(
                        nomPersonnage = etat.nomPersonnage,
                        dernierResultat = etat.dernierResultat,
                        onNomPersonnageChange = onNomPersonnageChange,
                        onOuvrirFiche = onOuvrirFiche,
                    )

                    HorizontalDivider()

                    ZoneDes(
                        libelleEtapeCourante = etapeCourante?.libelle,
                        expressionCourante = etapeCourante?.expression,
                        typeEtapeCourante = typeEtapeCourante,
                        modificateurFlat = etat.modificateurFlat,
                        avantage = etat.avantage,
                        estD20Unique = estD20Unique,
                        prochainsDes = etat.prochainsDes,
                        prochainEstTexte = etat.prochainEstTexte,
                        degatsDuTour = etat.degatsDuTour,
                        onIncrementerModificateurFlat = onIncrementerModificateurFlat,
                        onDefinirAvantage = onDefinirAvantage,
                        modifier = Modifier.weight(1f),
                    )

                    HorizontalDivider()

                    BarreBas(
                        degatsDuTour = etat.degatsDuTour,
                        onAnnuler = onAnnuler,
                        onRefaire = onRefaire,
                        onFinDuTour = onFinDuTour,
                    )
                }
            }
        }
    }
    }
}

@Preview(
    name = "Combat Screen — portrait",
    widthDp = 400,
    heightDp = 800,
    showBackground = true,
)
@Preview(
    name = "Combat Screen — paysage",
    widthDp = 800,
    heightDp = 400,
    showBackground = true,
)
@Composable
private fun CombatScreenPreview() {
    val attaque1 = Attaque.creer(
        nom = "Épée longue",
        etapes = listOf(
            EtapeDeJet(libelle = "Toucher", expression = "1d20+5", type = TypeJet.ATTAQUE),
            EtapeDeJet(libelle = "Dégâts", expression = "1d8+3", type = TypeJet.DEGATS),
        ),
    )
    val attaque2 = Attaque.creer(
        nom = "Dague",
        etapes = listOf(
            EtapeDeJet(libelle = "Toucher", expression = "1d20+3", type = TypeJet.ATTAQUE),
        ),
    )
    val etat = EtatCombat(
        attaques = listOf(attaque1, attaque2),
        idAttaqueSelectionnee = attaque1.id,
        indexEtapeCourante = 0,
        nomPersonnage = "Aragorn",
        avantage = TypeAvantage.AVANTAGE,
        degatsDuTour = 14,
        dernierResultat = ResultatJet(
            expression = "1d20+5",
            desTires = listOf(8, 15),
            modificateurExpression = 5,
            modificateurFlat = 0,
            total = 20,
            avantage = TypeAvantage.AVANTAGE,
        ),
        historique = listOf(
            EntreeHistorique(
                nomAttaque = "Épée longue",
                libelleEtape = "Dégâts",
                resultat = ResultatJet(
                    expression = "1d8+3",
                    desTires = listOf(6),
                    modificateurExpression = 3,
                    modificateurFlat = 0,
                    total = 9,
                    avantage = TypeAvantage.NORMAL,
                ),
                horodatageMs = 0L,
            ),
            EntreeHistorique(
                nomAttaque = "Épée longue",
                libelleEtape = "Toucher",
                resultat = ResultatJet(
                    expression = "1d20+5",
                    desTires = listOf(8, 15),
                    modificateurExpression = 5,
                    modificateurFlat = 0,
                    total = 20,
                    avantage = TypeAvantage.AVANTAGE,
                ),
                horodatageMs = 0L,
            ),
        ),
    )
    DnDCombatRollerTheme {
        CombatScreenContent(
            etat = etat,
            onSelectionnerAttaque = {},
            onAjouterAttaque = { _, _ -> },
            onModifierAttaque = {},
            onSupprimerAttaque = {},
            onDeplacerAttaque = { _, _ -> },
            onLancer = {},
            onIncrementerModificateurFlat = {},
            onDefinirAvantage = {},
            onNomPersonnageChange = {},
            onAnnuler = {},
            onRefaire = {},
            onFinDuTour = {},
            onOuvrirFiche = {},
        )
    }
}
