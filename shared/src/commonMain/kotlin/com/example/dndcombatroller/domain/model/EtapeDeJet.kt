package com.example.dndcombatroller.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class EtapeDeJet(
    val libelle: String,
    val expression: String,
    val type: TypeJet = TypeJet.AUTRE,
)
