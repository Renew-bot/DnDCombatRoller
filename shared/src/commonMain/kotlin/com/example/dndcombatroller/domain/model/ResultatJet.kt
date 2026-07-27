package com.example.dndcombatroller.domain.model

data class ResultatJet(
    val expression: String,
    val desTires: List<Int>,
    val modificateurExpression: Int,
    val modificateurFlat: Int,
    val total: Int,
    val avantage: TypeAvantage,
    val estNatural20: Boolean = false,
    val estNatural1: Boolean = false,
)
