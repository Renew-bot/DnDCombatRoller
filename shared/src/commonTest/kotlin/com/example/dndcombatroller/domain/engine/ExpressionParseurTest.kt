package com.example.dndcombatroller.domain.engine

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ExpressionParseurTest {

    @Test
    fun `parse expression simple`() {
        val r = ExpressionParseur.parse("1d20")
        assertIs<ResultatParsage.Succes>(r)
        assertEquals(1, r.groupes.size)
        assertEquals(1, r.groupes[0].nombre)
        assertEquals(20, r.groupes[0].faces)
        assertEquals(0, r.modificateur)
    }

    @Test
    fun `parse expression avec modificateur positif`() {
        val r = ExpressionParseur.parse("2d6+3")
        assertIs<ResultatParsage.Succes>(r)
        assertEquals(1, r.groupes.size)
        assertEquals(2, r.groupes[0].nombre)
        assertEquals(6, r.groupes[0].faces)
        assertEquals(3, r.modificateur)
    }

    @Test
    fun `parse expression avec modificateur negatif`() {
        val r = ExpressionParseur.parse("1d8-2")
        assertIs<ResultatParsage.Succes>(r)
        assertEquals(1, r.groupes.size)
        assertEquals(1, r.groupes[0].nombre)
        assertEquals(8, r.groupes[0].faces)
        assertEquals(-2, r.modificateur)
    }

    @Test
    fun `parse insensible a la casse`() {
        val r = ExpressionParseur.parse("1D20+4")
        assertIs<ResultatParsage.Succes>(r)
        assertEquals(20, r.groupes[0].faces)
        assertEquals(4, r.modificateur)
    }

    @Test
    fun `parse gere les espaces`() {
        val r = ExpressionParseur.parse(" 1 d 20 + 4 ")
        assertIs<ResultatParsage.Succes>(r)
        assertEquals(1, r.groupes[0].nombre)
        assertEquals(20, r.groupes[0].faces)
        assertEquals(4, r.modificateur)
    }

    @Test
    fun `parse expression multi-groupes sans modificateur`() {
        val r = ExpressionParseur.parse("2d4+1d6")
        assertIs<ResultatParsage.Succes>(r)
        assertEquals(2, r.groupes.size)
        assertEquals(ResultatParsage.GroupeDes(2, 4), r.groupes[0])
        assertEquals(ResultatParsage.GroupeDes(1, 6), r.groupes[1])
        assertEquals(0, r.modificateur)
    }

    @Test
    fun `parse expression multi-groupes avec modificateur`() {
        val r = ExpressionParseur.parse("4d10+1d8+4")
        assertIs<ResultatParsage.Succes>(r)
        assertEquals(2, r.groupes.size)
        assertEquals(ResultatParsage.GroupeDes(4, 10), r.groupes[0])
        assertEquals(ResultatParsage.GroupeDes(1, 8), r.groupes[1])
        assertEquals(4, r.modificateur)
    }

    @Test
    fun `parse expression trois groupes de des`() {
        val r = ExpressionParseur.parse("1d6+1d8+1d10")
        assertIs<ResultatParsage.Succes>(r)
        assertEquals(3, r.groupes.size)
        assertEquals(0, r.modificateur)
    }

    @Test
    fun `parse retourne erreur si expression invalide`() {
        val r = ExpressionParseur.parse("invalid")
        assertIs<ResultatParsage.Erreur>(r)
        assertTrue(r.message.isNotEmpty())
    }

    @Test
    fun `parse retourne erreur si nombre de des est zero`() {
        val r = ExpressionParseur.parse("0d6")
        assertIs<ResultatParsage.Erreur>(r)
    }

    @Test
    fun `parse retourne erreur si nombre de faces est zero`() {
        val r = ExpressionParseur.parse("1d0")
        assertIs<ResultatParsage.Erreur>(r)
    }

    @Test
    fun `parse retourne erreur si le nombre de des est absent`() {
        val r = ExpressionParseur.parse("d20")
        assertIs<ResultatParsage.Erreur>(r)
    }
}
