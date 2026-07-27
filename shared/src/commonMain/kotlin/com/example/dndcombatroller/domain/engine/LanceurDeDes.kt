package com.example.dndcombatroller.domain.engine

import com.example.dndcombatroller.domain.model.ResultatJet
import com.example.dndcombatroller.domain.model.TypeAvantage

class LanceurDeDes(private val source: SourceAleatoire = SourceAleatoireParDefaut) {

    fun lance(
        expression: String,
        avantage: TypeAvantage = TypeAvantage.NORMAL,
        modificateurFlat: Int = 0,
    ): ResultatJet {
        val succes = when (val p = ExpressionParseur.parse(expression)) {
            is ResultatParsage.Succes -> p
            is ResultatParsage.Erreur -> throw IllegalArgumentException(p.message)
        }

        val groupes = succes.groupes
        val estD20Unique = groupes.size == 1 && groupes[0].nombre == 1 && groupes[0].faces == 20
        val avantageEffectif = if (estD20Unique) avantage else TypeAvantage.NORMAL

        val desTires: List<Int>
        val valeurGardee: Int

        if (avantageEffectif != TypeAvantage.NORMAL) {
            val de1 = source.nextInt(1, groupes[0].faces)
            val de2 = source.nextInt(1, groupes[0].faces)
            desTires = listOf(de1, de2)
            valeurGardee = when (avantageEffectif) {
                TypeAvantage.AVANTAGE -> maxOf(de1, de2)
                TypeAvantage.DESAVANTAGE -> minOf(de1, de2)
                TypeAvantage.NORMAL -> de1 // inaccessible
            }
        } else {
            desTires = groupes.flatMap { g -> List(g.nombre) { source.nextInt(1, g.faces) } }
            valeurGardee = desTires.sum()
        }

        return ResultatJet(
            expression = expression,
            desTires = desTires,
            modificateurExpression = succes.modificateur,
            modificateurFlat = modificateurFlat,
            total = valeurGardee + succes.modificateur + modificateurFlat,
            avantage = avantageEffectif,
            estNatural20 = estD20Unique && valeurGardee == 20,
            estNatural1 = estD20Unique && valeurGardee == 1,
        )
    }
}
