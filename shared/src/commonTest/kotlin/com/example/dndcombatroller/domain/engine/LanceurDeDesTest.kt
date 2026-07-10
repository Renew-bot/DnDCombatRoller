package com.example.dndcombatroller.domain.engine

import com.example.dndcombatroller.domain.model.TypeAvantage
import kotlin.test.Test
import kotlin.test.assertEquals

class LanceurDeDesTest {

    private fun sourceFixe(vararg valeurs: Int): SourceAleatoire {
        val iter = valeurs.iterator()
        return object : SourceAleatoire {
            override fun nextInt(borneMin: Int, borneMax: Int): Int = iter.nextInt()
        }
    }

    @Test
    fun `jet simple retourne le bon total`() {
        val lanceur = LanceurDeDes(sourceFixe(4))
        val r = lanceur.lance("1d6")
        assertEquals(listOf(4), r.desTires)
        assertEquals(0, r.modificateurExpression)
        assertEquals(0, r.modificateurFlat)
        assertEquals(4, r.total)
    }

    @Test
    fun `jet avec modificateur d expression`() {
        val lanceur = LanceurDeDes(sourceFixe(5))
        val r = lanceur.lance("1d8+3")
        assertEquals(listOf(5), r.desTires)
        assertEquals(3, r.modificateurExpression)
        assertEquals(8, r.total)
    }

    @Test
    fun `jet multi-des somme tous les des`() {
        val lanceur = LanceurDeDes(sourceFixe(3, 5))
        val r = lanceur.lance("2d6")
        assertEquals(listOf(3, 5), r.desTires)
        assertEquals(8, r.total)
    }

    @Test
    fun `avantage garde le plus haut d20`() {
        val lanceur = LanceurDeDes(sourceFixe(8, 15))
        val r = lanceur.lance("1d20", TypeAvantage.AVANTAGE)
        assertEquals(listOf(8, 15), r.desTires)
        assertEquals(TypeAvantage.AVANTAGE, r.avantage)
        assertEquals(15, r.total)
    }

    @Test
    fun `desavantage garde le plus bas d20`() {
        val lanceur = LanceurDeDes(sourceFixe(15, 8))
        val r = lanceur.lance("1d20", TypeAvantage.DESAVANTAGE)
        assertEquals(listOf(15, 8), r.desTires)
        assertEquals(TypeAvantage.DESAVANTAGE, r.avantage)
        assertEquals(8, r.total)
    }

    @Test
    fun `avantage ignore pour un jet non d20`() {
        val lanceur = LanceurDeDes(sourceFixe(3, 4))
        val r = lanceur.lance("2d6", TypeAvantage.AVANTAGE)
        assertEquals(listOf(3, 4), r.desTires)
        assertEquals(TypeAvantage.NORMAL, r.avantage)
        assertEquals(7, r.total)
    }

    @Test
    fun `modificateur flat s ajoute au total`() {
        val lanceur = LanceurDeDes(sourceFixe(3))
        val r = lanceur.lance("1d6+2", modificateurFlat = 1)
        assertEquals(2, r.modificateurExpression)
        assertEquals(1, r.modificateurFlat)
        assertEquals(6, r.total) // 3 + 2 + 1
    }

    @Test
    fun `avantage sur d20 avec modificateur d expression`() {
        val lanceur = LanceurDeDes(sourceFixe(12, 18))
        val r = lanceur.lance("1d20+5", TypeAvantage.AVANTAGE)
        assertEquals(listOf(12, 18), r.desTires)
        assertEquals(TypeAvantage.AVANTAGE, r.avantage)
        assertEquals(23, r.total) // max(12,18) + 5
    }
}
