package com.example.dndcombatroller.ui

import com.example.dndcombatroller.domain.model.ResultatJet

data class EntreeHistorique(
    val nomAttaque: String,
    val libelleEtape: String,
    val resultat: ResultatJet?,
    val horodatageMs: Long,
    val texte: String? = null,
)
