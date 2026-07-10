package com.example.dndcombatroller.domain.model

import com.example.dndcombatroller.domain.generateId
import kotlinx.serialization.Serializable

@Serializable
data class Attaque(
    val id: String,
    val nom: String,
    val etapes: List<EtapeDeJet>,
) {
    companion object {
        fun creer(nom: String, etapes: List<EtapeDeJet>): Attaque =
            Attaque(id = generateId(), nom = nom, etapes = etapes)
    }
}
