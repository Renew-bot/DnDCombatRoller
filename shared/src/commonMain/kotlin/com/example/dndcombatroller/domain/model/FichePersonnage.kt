package com.example.dndcombatroller.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Caracteristique(
    val nom: String,
    val mod: String,
    val score: String,
)

@Serializable
data class LigneMaitrise(
    val nom: String,
    val mod: String,
    val maitrise: Boolean,
)

@Serializable
data class FichePersonnage(
    val nom: String,
    val classeNiveau: String,
    val race: String,
    val ca: String,
    val initiative: String,
    val vitesse: String,
    val pvMax: String,
    val perceptionPassive: String,
    val caracteristiques: List<Caracteristique>,
    val sauvegardes: List<LigneMaitrise>,
    val competences: List<LigneMaitrise>,
)
