package com.example.dndcombatroller.ui

import com.example.dndcombatroller.domain.engine.LanceurDeDes
import com.example.dndcombatroller.domain.engine.SourceAleatoire
import com.example.dndcombatroller.domain.model.Attaque
import com.example.dndcombatroller.domain.model.EtapeDeJet
import com.example.dndcombatroller.domain.model.TypeJet
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CombatViewModelTest {

    private fun sourceFixe(vararg valeurs: Int): SourceAleatoire {
        val iter = valeurs.iterator()
        return object : SourceAleatoire {
            override fun nextInt(borneMin: Int, borneMax: Int): Int = iter.nextInt()
        }
    }

    private fun creerViewModel(vararg des: Int): CombatViewModel =
        CombatViewModel(lanceur = LanceurDeDes(sourceFixe(*des)), horloge = { 0L })

    private val attaqueDeux = Attaque.creer(
        nom = "Épée longue",
        etapes = listOf(
            EtapeDeJet(libelle = "Toucher", expression = "1d20+5", type = TypeJet.ATTAQUE),
            EtapeDeJet(libelle = "Dégâts", expression = "1d8+3", type = TypeJet.DEGATS),
        ),
    )

    private val attaqueUne = Attaque.creer(
        nom = "Dague",
        etapes = listOf(
            EtapeDeJet(libelle = "Toucher", expression = "1d20+3", type = TypeJet.ATTAQUE),
        ),
    )

    // --- lancer() : avancement d'étape ---

    @Test
    fun `lancer avance a l etape suivante quand elle existe`() {
        // d20 pour toucher (étape 0)
        val vm = creerViewModel(10)
        vm.ajouterAttaque(attaqueDeux)
        vm.selectionnerAttaque(attaqueDeux.id)
        assertEquals(0, vm.uiState.value.indexEtapeCourante)

        vm.lancer()

        assertEquals(1, vm.uiState.value.indexEtapeCourante)
    }

    @Test
    fun `lancer apres la derniere etape avance l index au-dela pour vider la carte`() {
        // étape 0 : d20 ; étape 1 : d8 → après étape 1 (dernière), index = 2 (hors-bornes → null)
        val vm = creerViewModel(10, 5)
        vm.ajouterAttaque(attaqueDeux)
        vm.selectionnerAttaque(attaqueDeux.id)

        vm.lancer() // étape 0 → avance à 1
        vm.lancer() // étape 1 (dernière) → avance à 2 (hors-bornes)

        assertEquals(2, vm.uiState.value.indexEtapeCourante)
        assertNull(vm.uiState.value.attaqueSelectionnee?.etapes?.getOrNull(vm.uiState.value.indexEtapeCourante))
    }

    @Test
    fun `lancer avec une seule etape avance l index au-dela pour vider la carte`() {
        val vm = creerViewModel(15)
        vm.ajouterAttaque(attaqueUne)
        vm.selectionnerAttaque(attaqueUne.id)

        vm.lancer()

        assertEquals(1, vm.uiState.value.indexEtapeCourante)
        assertNull(vm.uiState.value.attaqueSelectionnee?.etapes?.getOrNull(vm.uiState.value.indexEtapeCourante))
    }

    // --- lancer() : cumul des dégâts ---

    @Test
    fun `lancer cumule les degats pour les etapes de type DEGATS`() {
        // d20 pour toucher, d8 pour dégâts (valeur 6 + modExpr 3 = 9)
        val vm = creerViewModel(10, 6)
        vm.ajouterAttaque(attaqueDeux)
        vm.selectionnerAttaque(attaqueDeux.id)

        vm.lancer() // ATTAQUE → degatsDuTour reste 0
        assertEquals(0, vm.uiState.value.degatsDuTour)

        vm.lancer() // DEGATS : 6 + 3 = 9
        assertEquals(9, vm.uiState.value.degatsDuTour)
    }

    @Test
    fun `lancer n ajoute pas les degats pour les etapes de type ATTAQUE`() {
        val vm = creerViewModel(12)
        vm.ajouterAttaque(attaqueUne)
        vm.selectionnerAttaque(attaqueUne.id)

        vm.lancer()

        assertEquals(0, vm.uiState.value.degatsDuTour)
    }

    @Test
    fun `lancer accumule les degats de plusieurs tours`() {
        val degatsEtape = EtapeDeJet(libelle = "Dégâts", expression = "1d6", type = TypeJet.DEGATS)
        val attaque = Attaque.creer(nom = "Sort", etapes = listOf(degatsEtape))
        // deux jets : 4 puis 3
        val vm = creerViewModel(4, 3)
        vm.ajouterAttaque(attaque)
        vm.selectionnerAttaque(attaque.id)

        vm.lancer() // 4
        vm.selectionnerAttaque(attaque.id) // réinitialise l'étape mais pas degatsDuTour
        vm.lancer() // 3

        assertEquals(7, vm.uiState.value.degatsDuTour)
    }

    // --- lancer() : historique et dernierResultat ---

    @Test
    fun `lancer ajoute une entree dans l historique`() {
        val vm = creerViewModel(8)
        vm.ajouterAttaque(attaqueUne)
        vm.selectionnerAttaque(attaqueUne.id)

        vm.lancer()

        assertEquals(1, vm.uiState.value.historique.size)
        val entree = vm.uiState.value.historique[0]
        assertEquals("Dague", entree.nomAttaque)
        assertEquals("Toucher", entree.libelleEtape)
    }

    @Test
    fun `lancer met a jour dernierResultat`() {
        val vm = creerViewModel(7)
        vm.ajouterAttaque(attaqueUne)
        vm.selectionnerAttaque(attaqueUne.id)

        assertNull(vm.uiState.value.dernierResultat)
        vm.lancer()

        val resultat = vm.uiState.value.dernierResultat!!
        assertEquals(listOf(7), resultat.desTires)
        assertEquals(10, resultat.total) // 7 + modExpr 3
    }

    @Test
    fun `lancer sans attaque selectionnee ne modifie pas l etat`() {
        val vm = creerViewModel()
        val etatAvant = vm.uiState.value

        vm.lancer()

        assertEquals(etatAvant, vm.uiState.value)
    }

    // --- finDuTour() ---

    @Test
    fun `finDuTour remet degatsDuTour a zero`() {
        val vm = creerViewModel(5, 4)
        vm.ajouterAttaque(attaqueDeux)
        vm.selectionnerAttaque(attaqueDeux.id)
        vm.lancer() // ATTAQUE
        vm.lancer() // DEGATS : 4 + 3 = 7

        vm.finDuTour()

        assertEquals(0, vm.uiState.value.degatsDuTour)
    }

    @Test
    fun `finDuTour reinitialise la selection et l etape`() {
        val vm = creerViewModel(5, 4)
        vm.ajouterAttaque(attaqueDeux)
        vm.selectionnerAttaque(attaqueDeux.id)
        vm.lancer() // avance à étape 1

        vm.finDuTour()

        assertNull(vm.uiState.value.idAttaqueSelectionnee)
        assertEquals(0, vm.uiState.value.indexEtapeCourante)
    }

    // --- annuler() / refaire() ---

    @Test
    fun `annuler restaure l etat precedent`() {
        val vm = creerViewModel()
        vm.ajouterAttaque(attaqueUne)
        val etatAvantSelection = vm.uiState.value

        vm.selectionnerAttaque(attaqueUne.id)

        vm.annuler()

        assertEquals(etatAvantSelection, vm.uiState.value)
    }

    @Test
    fun `refaire reapplique l etat annule`() {
        val vm = creerViewModel()
        vm.ajouterAttaque(attaqueUne)
        vm.selectionnerAttaque(attaqueUne.id)
        val etatApresSelection = vm.uiState.value

        vm.annuler()
        vm.refaire()

        assertEquals(etatApresSelection, vm.uiState.value)
    }

    @Test
    fun `annuler ne fait rien si la pile est vide`() {
        val vm = creerViewModel()
        val etatInitial = vm.uiState.value

        vm.annuler()

        assertEquals(etatInitial, vm.uiState.value)
    }
}
