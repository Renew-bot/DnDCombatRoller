package com.example.dndcombatroller.domain.engine

object ExpressionParseur {

    // Un terme : signe optionnel, chiffres, puis optionnellement 'd' + chiffres
    private val TERME = Regex("""[+-]?\d+(?:d\d+)?""")
    // Valide que toute la chaîne ne contient que des termes valides collés
    private val EXPRESSION_VALIDE = Regex("""^[+-]?\d+(?:d\d+)?(?:[+-]\d+(?:d\d+)?)*$""")

    fun parse(expression: String): ResultatParsage {
        val norm = expression.replace(Regex("""\s"""), "").lowercase()
        if (norm.isEmpty()) return ResultatParsage.Erreur("Expression vide")

        if (!EXPRESSION_VALIDE.matches(norm)) {
            return ResultatParsage.Erreur(
                "Expression invalide : \"$expression\". Format attendu : 1d20+5, 2d4+1d6, 4d10+1d8+4"
            )
        }

        val groupes = mutableListOf<ResultatParsage.GroupeDes>()
        var modificateur = 0

        for (match in TERME.findAll(norm)) {
            val terme = match.value
            val dIdx = terme.indexOf('d')
            if (dIdx >= 0) {
                val nombre = terme.substring(0, dIdx).trimStart('+').toInt()
                val faces = terme.substring(dIdx + 1).toInt()
                if (nombre <= 0) return ResultatParsage.Erreur("Le nombre de dés doit être ≥ 1 (reçu : $nombre)")
                if (faces <= 0) return ResultatParsage.Erreur("Le nombre de faces doit être ≥ 1 (reçu : $faces)")
                groupes.add(ResultatParsage.GroupeDes(nombre, faces))
            } else {
                modificateur += terme.toInt()
            }
        }

        if (groupes.isEmpty()) return ResultatParsage.Erreur("L'expression doit contenir au moins un dé")
        return ResultatParsage.Succes(groupes, modificateur)
    }
}
