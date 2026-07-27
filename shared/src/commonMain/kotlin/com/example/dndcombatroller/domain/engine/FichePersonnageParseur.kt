package com.example.dndcombatroller.domain.engine

import com.example.dndcombatroller.domain.model.Caracteristique
import com.example.dndcombatroller.domain.model.FichePersonnage
import com.example.dndcombatroller.domain.model.LigneMaitrise

/**
 * Parse l'export HTML "Feuille de personnage" d'aidedd.org (sheetPrint.php) : chaque section
 * du formulaire est un `<div class="block bNN">`, retrouvé par sa classe puis découpé en une
 * fenêtre de texte suffisamment large pour y appliquer une regex ciblée.
 */
object FichePersonnageParseur {

    fun parse(html: String): FichePersonnage? {
        fun bloc(id: String, longueur: Int): String {
            val debut = Regex("""class="block $id"""").find(html)?.range?.first ?: return ""
            return html.substring(debut, minOf(html.length, debut + longueur))
        }

        val b2 = bloc("b2", 400)
        val nom = Regex(
            """class="textinput" value="([^"]*)">\s*</div>\s*<div class="line line2">\s*Nom du personnage""",
        ).find(b2)?.groupValues?.get(1)?.takeIf { it.isNotBlank() } ?: "Personnage"

        val b3 = bloc("b3", 1200)
        val b3Inputs = Regex("""class="textinput"[^>]*value="([^"]*)"""").findAll(b3).map { it.groupValues[1] }.toList()
        val classeNiveau = b3Inputs.getOrElse(0) { "" }
        val race = b3Inputs.getOrElse(3) { "" }

        val b5 = bloc("b5", 2600)
        val abilRegex = Regex(
            """<span class="field">(Force|Dextérité|Constitution|Intelligence|Sagesse|Charisme)</span></div><div class="line2"><input[^>]*value="([^"]*)"></div><div class="line3"><input[^>]*value="([^"]*)">""",
        )
        val caracteristiques = abilRegex.findAll(b5)
            .map { m -> Caracteristique(nom = m.groupValues[1], mod = m.groupValues[2], score = m.groupValues[3]) }
            .toList()

        val rowRegex = Regex(
            """<div class="line( line[123])?">&nbsp;<input type="checkbox" data-pos="0"( checked="checked")?> &nbsp;<input data-pos="0" class="textinput" maxlength="3" value="([^"]*)"> &nbsp;<span class="field">([^<]+)</span></div>""",
        )
        val b15 = bloc("b15", 1500)
        val sauvegardes = rowRegex.findAll(b15)
            .map { m -> LigneMaitrise(nom = m.groupValues[4], mod = m.groupValues[3], maitrise = m.groupValues[2].isNotEmpty()) }
            .toList()
        val b16 = bloc("b16", 4000)
        val competences = rowRegex.findAll(b16)
            .map { m -> LigneMaitrise(nom = m.groupValues[4], mod = m.groupValues[3], maitrise = m.groupValues[2].isNotEmpty()) }
            .toList()

        val b17 = bloc("b17", 400)
        val perceptionPassive = Regex("""value="([^"]*)"""").find(b17)?.groupValues?.get(1) ?: ""

        val b20 = bloc("b20", 900)
        val casVals = Regex("""class="textinput" maxlength="6" value="([^"]*)"""").findAll(b20).map { it.groupValues[1] }.toList()
        val ca = casVals.getOrElse(0) { "" }
        val initiative = casVals.getOrElse(1) { "" }
        val vitesse = casVals.getOrElse(2) { "" }

        val b24 = bloc("b24", 500)
        val pvMax = Regex("""Maximum de points de vie</span>\s*<input[^>]*value="([^"]*)"""").find(b24)?.groupValues?.get(1) ?: ""

        if (caracteristiques.isEmpty()) return null

        return FichePersonnage(
            nom = nom,
            classeNiveau = classeNiveau,
            race = race,
            ca = ca,
            initiative = initiative,
            vitesse = vitesse,
            pvMax = pvMax,
            perceptionPassive = perceptionPassive,
            caracteristiques = caracteristiques,
            sauvegardes = sauvegardes,
            competences = competences,
        )
    }
}
