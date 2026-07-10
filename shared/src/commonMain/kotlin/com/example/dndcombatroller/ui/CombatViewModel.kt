@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.example.dndcombatroller.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dndcombatroller.data.CombatRepository
import com.example.dndcombatroller.data.InMemoryCombatRepository
import com.example.dndcombatroller.domain.engine.LanceurDeDes
import com.example.dndcombatroller.domain.model.Attaque
import com.example.dndcombatroller.domain.model.EtapeDeJet
import com.example.dndcombatroller.domain.model.TypeAvantage
import com.example.dndcombatroller.domain.model.TypeJet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CombatViewModel(
    private val repository: CombatRepository = InMemoryCombatRepository(),
    private val lanceur: LanceurDeDes = LanceurDeDes(),
    private val horloge: () -> Long = { kotlin.time.Clock.System.now().toEpochMilliseconds() },
) : ViewModel() {

    private val _uiState = MutableStateFlow(EtatCombat(attaques = listOf(attaqueDelugeDeCoups())))
    val uiState: StateFlow<EtatCombat> = _uiState.asStateFlow()

    private val pileAnnuler = mutableListOf<EtatCombat>()
    private val pileRefaire = mutableListOf<EtatCombat>()

    init {
        lancerSuspend {
            val attaquesChargees = repository.chargerAttaques()
            val nom = repository.chargerNomPersonnage()
            // Remplace l'état initial uniquement si le dépôt contient des données persistées
            if (attaquesChargees.isNotEmpty() || nom.isNotEmpty()) {
                _uiState.value = EtatCombat(
                    attaques = attaquesChargees.ifEmpty { listOf(attaqueDelugeDeCoups()) },
                    nomPersonnage = nom,
                )
            }
        }
    }

    // viewModelScope indisponible sur JVM sans Dispatchers.Main (tests) — fallback silencieux
    private fun lancerSuspend(block: suspend () -> Unit) {
        try {
            viewModelScope.launch { block() }
        } catch (_: IllegalStateException) {}
    }

    private fun appliquer(transform: (EtatCombat) -> EtatCombat) {
        val avant = _uiState.value
        pileAnnuler += avant
        pileRefaire.clear()
        _uiState.value = transform(avant)
        val apres = _uiState.value
        if (avant.attaques != apres.attaques) {
            lancerSuspend { repository.sauvegarderAttaques(apres.attaques) }
        }
    }

    fun selectionnerAttaque(id: String) = appliquer { etat ->
        etat.copy(
            idAttaqueSelectionnee = id,
            indexEtapeCourante = 0,
            avantage = TypeAvantage.NORMAL,
            modificateurFlat = 0,
        )
    }

    fun definirAvantage(avantage: TypeAvantage) = appliquer { etat ->
        etat.copy(avantage = avantage)
    }

    fun incrementerModificateurFlat(delta: Int) = appliquer { etat ->
        etat.copy(modificateurFlat = etat.modificateurFlat + delta)
    }

    fun definirModificateurFlat(valeur: Int) = appliquer { etat ->
        etat.copy(modificateurFlat = valeur)
    }

    fun lancer() = appliquer { etat ->
        val attaque = etat.attaqueSelectionnee ?: return@appliquer etat
        val etape = attaque.etapes.getOrNull(etat.indexEtapeCourante) ?: return@appliquer etat

        if (etape.type == TypeJet.AUTRE) {
            val entree = EntreeHistorique(
                nomAttaque = attaque.nom,
                libelleEtape = etape.libelle,
                resultat = null,
                texte = etape.expression,
                horodatageMs = horloge(),
            )
            return@appliquer etat.copy(
                indexEtapeCourante = etat.indexEtapeCourante + 1,
                historique = etat.historique + entree,
            )
        }

        val resultat = lanceur.lance(
            expression = etape.expression,
            avantage = etat.avantage,
            modificateurFlat = etat.modificateurFlat,
        )

        val entree = EntreeHistorique(
            nomAttaque = attaque.nom,
            libelleEtape = etape.libelle,
            resultat = resultat,
            horodatageMs = horloge(),
        )

        val degatsDuTour = if (etape.type == TypeJet.DEGATS) {
            etat.degatsDuTour + resultat.total
        } else {
            etat.degatsDuTour
        }

        val indexSuivant = etat.indexEtapeCourante + 1

        etat.copy(
            dernierResultat = resultat,
            historique = etat.historique + entree,
            degatsDuTour = degatsDuTour,
            indexEtapeCourante = indexSuivant,
        )
    }

    fun finDuTour() = appliquer { etat ->
        etat.copy(
            degatsDuTour = 0,
            idAttaqueSelectionnee = null,
            indexEtapeCourante = 0,
        )
    }

    fun ajouterAttaque(attaque: Attaque) = appliquer { etat ->
        etat.copy(attaques = etat.attaques + attaque)
    }

    fun modifierAttaque(attaque: Attaque) = appliquer { etat ->
        etat.copy(attaques = etat.attaques.map { if (it.id == attaque.id) attaque else it })
    }

    fun supprimerAttaque(id: String) = appliquer { etat ->
        etat.copy(
            attaques = etat.attaques.filter { it.id != id },
            idAttaqueSelectionnee = if (etat.idAttaqueSelectionnee == id) null else etat.idAttaqueSelectionnee,
        )
    }

    fun deplacerAttaque(de: Int, vers: Int) = appliquer { etat ->
        val liste = etat.attaques.toMutableList()
        liste.add(vers, liste.removeAt(de))
        etat.copy(attaques = liste)
    }

    fun definirNomPersonnage(nom: String) {
        // Pas dans appliquer : chaque frappe ne doit pas encombrer la pile d'annulation
        _uiState.value = _uiState.value.copy(nomPersonnage = nom)
        lancerSuspend { repository.sauvegarderNomPersonnage(nom) }
    }

    fun annuler() {
        if (pileAnnuler.isEmpty()) return
        val avant = _uiState.value
        pileRefaire += avant
        _uiState.value = pileAnnuler.removeLast()
        val apres = _uiState.value
        if (avant.attaques != apres.attaques) {
            lancerSuspend { repository.sauvegarderAttaques(apres.attaques) }
        }
    }

    fun refaire() {
        if (pileRefaire.isEmpty()) return
        val avant = _uiState.value
        pileAnnuler += avant
        _uiState.value = pileRefaire.removeLast()
        val apres = _uiState.value
        if (avant.attaques != apres.attaques) {
            lancerSuspend { repository.sauvegarderAttaques(apres.attaques) }
        }
    }

    private companion object {
        fun attaqueDelugeDeCoups() = Attaque.creer(
            nom = "Déluge de coups",
            etapes = listOf(
                EtapeDeJet(libelle = "Jet d'attaque", expression = "1d20+4", type = TypeJet.ATTAQUE),
                EtapeDeJet(libelle = "Dégâts", expression = "1d6+4", type = TypeJet.DEGATS),
            ),
        )
    }
}
