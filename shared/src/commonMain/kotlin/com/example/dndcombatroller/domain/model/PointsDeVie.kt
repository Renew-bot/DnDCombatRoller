package com.example.dndcombatroller.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PointsDeVie(
    val actuels: Int = 0,
    val temporaires: Int = 0,
)
