package com.example.dndcombatroller.data

import androidx.compose.runtime.staticCompositionLocalOf
import com.example.dndcombatroller.domain.model.Attaque
import com.example.dndcombatroller.domain.model.FichePersonnage
import com.example.dndcombatroller.domain.model.PointsDeVie

interface CombatRepository {
    suspend fun chargerAttaques(): List<Attaque>
    suspend fun sauvegarderAttaques(attaques: List<Attaque>)
    suspend fun chargerNomPersonnage(): String
    suspend fun sauvegarderNomPersonnage(nom: String)
    suspend fun chargerFiche(): FichePersonnage?
    suspend fun sauvegarderFiche(fiche: FichePersonnage)
    suspend fun chargerPointsDeVie(): PointsDeVie
    suspend fun sauvegarderPointsDeVie(pointsDeVie: PointsDeVie)
}

class InMemoryCombatRepository : CombatRepository {
    override suspend fun chargerAttaques(): List<Attaque> = emptyList()
    override suspend fun sauvegarderAttaques(attaques: List<Attaque>) = Unit
    override suspend fun chargerNomPersonnage(): String = ""
    override suspend fun sauvegarderNomPersonnage(nom: String) = Unit
    override suspend fun chargerFiche(): FichePersonnage? = null
    override suspend fun sauvegarderFiche(fiche: FichePersonnage) = Unit
    override suspend fun chargerPointsDeVie(): PointsDeVie = PointsDeVie()
    override suspend fun sauvegarderPointsDeVie(pointsDeVie: PointsDeVie) = Unit
}

val LocalCombatRepository = staticCompositionLocalOf<CombatRepository> { InMemoryCombatRepository() }

/** Déclenche la sélection d'un fichier par l'utilisateur et transmet son contenu texte. */
fun interface SelecteurFichier {
    fun ouvrir(onTexteCharge: (String) -> Unit)
}

val LocalSelecteurFichier = staticCompositionLocalOf<SelecteurFichier> { SelecteurFichier { } }
