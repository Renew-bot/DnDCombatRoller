package com.example.dndcombatroller.domain.engine

import kotlin.random.Random

interface SourceAleatoire {
    /** Retourne un entier dans [borneMin, borneMax] inclus. */
    fun nextInt(borneMin: Int, borneMax: Int): Int
}

internal object SourceAleatoireParDefaut : SourceAleatoire {
    override fun nextInt(borneMin: Int, borneMax: Int): Int =
        Random.nextInt(borneMin, borneMax + 1)
}
