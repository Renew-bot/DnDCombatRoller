package com.example.dndcombatroller.ui

import com.example.dndcombatroller.domain.model.Attaque
import com.example.dndcombatroller.domain.model.FichePersonnage
import com.example.dndcombatroller.domain.model.ResultatJet
import com.example.dndcombatroller.domain.model.TypeAvantage
import com.example.dndcombatroller.domain.model.TypeJet

data class EtatCombat(
    val attaques: List<Attaque> = emptyList(),
    val idAttaqueSelectionnee: String? = null,
    val indexEtapeCourante: Int = 0,
    val avantage: TypeAvantage = TypeAvantage.NORMAL,
    val modificateurFlat: Int = 0,
    val dernierResultat: ResultatJet? = null,
    val historique: List<EntreeHistorique> = emptyList(),
    val degatsDuTour: Int = 0,
    val nomPersonnage: String = "",
    val fiche: FichePersonnage? = null,
    val pvActuel: Int = 0,
    val pvTemporaires: Int = 0,
) {
    val attaqueSelectionnee: Attaque?
        get() = attaques.find { it.id == idAttaqueSelectionnee }

    val prochainsDes: String?
        get() = attaqueSelectionnee?.etapes?.getOrNull(indexEtapeCourante + 1)?.expression

    val prochainEstTexte: Boolean
        get() = attaqueSelectionnee?.etapes?.getOrNull(indexEtapeCourante + 1)?.type == TypeJet.AUTRE
}
