package com.example.dndcombatroller.data

import androidx.compose.runtime.staticCompositionLocalOf
import com.example.dndcombatroller.domain.model.Attaque

interface CombatRepository {
    suspend fun chargerAttaques(): List<Attaque>
    suspend fun sauvegarderAttaques(attaques: List<Attaque>)
    suspend fun chargerNomPersonnage(): String
    suspend fun sauvegarderNomPersonnage(nom: String)
}

class InMemoryCombatRepository : CombatRepository {
    override suspend fun chargerAttaques(): List<Attaque> = emptyList()
    override suspend fun sauvegarderAttaques(attaques: List<Attaque>) = Unit
    override suspend fun chargerNomPersonnage(): String = ""
    override suspend fun sauvegarderNomPersonnage(nom: String) = Unit
}

val LocalCombatRepository = staticCompositionLocalOf<CombatRepository> { InMemoryCombatRepository() }
