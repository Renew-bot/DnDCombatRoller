package com.example.dndcombatroller.domain.engine

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FichePersonnageParseurTest {

    @Test
    fun `parse export aidedd renvoie les champs attendus`() {
        val fiche = assertNotNull(FichePersonnageParseur.parse(EXTRAIT_AIDEDD))
        assertEquals("Lupus", fiche.nom)
        assertEquals("Moine 4", fiche.classeNiveau)
        assertEquals("Féral (h)", fiche.race)
        assertEquals("16", fiche.ca)
        assertEquals("+3", fiche.initiative)
        assertEquals("12 m", fiche.vitesse)
        assertEquals("19", fiche.pvMax)
        assertEquals("12", fiche.perceptionPassive)

        assertEquals(6, fiche.caracteristiques.size)
        assertEquals("Force", fiche.caracteristiques[0].nom)
        assertEquals("+0", fiche.caracteristiques[0].mod)
        assertEquals("10", fiche.caracteristiques[0].score)
        assertEquals("Dextérité", fiche.caracteristiques[1].nom)
        assertEquals("+3", fiche.caracteristiques[1].mod)
        assertEquals("17", fiche.caracteristiques[1].score)

        assertEquals(6, fiche.sauvegardes.size)
        assertEquals("Force", fiche.sauvegardes[0].nom)
        assertEquals("+2", fiche.sauvegardes[0].mod)
        assertTrue(fiche.sauvegardes[0].maitrise)
        assertEquals("Constitution", fiche.sauvegardes[2].nom)
        assertTrue(!fiche.sauvegardes[2].maitrise)

        assertEquals(18, fiche.competences.size)
        assertEquals("Acrobaties (Dex)", fiche.competences[0].nom)
        assertEquals("+5", fiche.competences[0].mod)
        assertTrue(fiche.competences[0].maitrise)
        assertEquals("Arcanes (Int)", fiche.competences[1].nom)
        assertTrue(!fiche.competences[1].maitrise)
    }

    @Test
    fun `parse contenu sans caracteristiques renvoie null`() {
        assertNull(FichePersonnageParseur.parse("<html><body>rien à voir ici</body></html>"))
    }

    private companion object {
        // Extrait fidèle (mêmes blocs bNN) d'un export aidedd.org/dnd-builder/sheetPrint.php
        val EXTRAIT_AIDEDD = """
            <div class="block b2" style="width:34.5%">
                <div class="line line1">
                    <input data-pos="0" class="textinput" value="Lupus">
                </div>
                <div class="line line2">Nom du personnage</div>
            </div>
            <div class="block b3" style="width:56%">
                <div class="line line1">
                    <input data-pos="0" class="textinput" style="width:40%" value="Moine 4">
                    <input data-pos="0" class="textinput" style="width:25%" value="Voyageur">
                    <input data-pos="0" class="textinput" style="width:25%" value="">
                </div><div class="line">
                    <span class="field" style="width:40%">Classe &amp; Niveau</span>
                    <span class="field" style="width:25%">Historique</span>
                    <span class="field" style="width:25%">Nom du joueur</span>
                </div><div class="line line2">
                    <input data-pos="0" class="textinput" style="width:40%" value="Féral (h)">
                    <input data-pos="0" class="textinput" style="width:25%" value="Neutre bon">
                    <input data-pos="0" class="textinput" style="width:25%" value="570">
                </div>
            </div>
            <div class="block b5" style="width:36%">
            <div class="block b6"><div class="line1"><span class="field">Force</span></div><div class="line2"><input data-pos="0" class="textinput" maxlength="3" value="+0"></div><div class="line3"><input data-pos="0" class="textinput" maxlength="2" value="10"></div></div><div class="block b7"><div class="line1"><span class="field">Dextérité</span></div><div class="line2"><input data-pos="0" class="textinput" maxlength="3" value="+3"></div><div class="line3"><input data-pos="0" class="textinput" maxlength="2" value="17"></div></div><div class="block b7"><div class="line1"><span class="field">Constitution</span></div><div class="line2"><input data-pos="0" class="textinput" maxlength="3" value="+3"></div><div class="line3"><input data-pos="0" class="textinput" maxlength="2" value="16"></div></div><div class="block b7"><div class="line1"><span class="field">Intelligence</span></div><div class="line2"><input data-pos="0" class="textinput" maxlength="3" value="+0"></div><div class="line3"><input data-pos="0" class="textinput" maxlength="2" value="10"></div></div><div class="block b7"><div class="line1"><span class="field">Sagesse</span></div><div class="line2"><input data-pos="0" class="textinput" maxlength="3" value="+2"></div><div class="line3"><input data-pos="0" class="textinput" maxlength="2" value="15"></div></div><div class="block b8"><div class="line1"><span class="field">Charisme</span></div><div class="line2"><input data-pos="0" class="textinput" maxlength="3" value="+0"></div><div class="line3"><input data-pos="0" class="textinput" maxlength="2" value="10"></div></div>			</div>
            <div class="block b15">
            <div class="line line1">&nbsp;<input type="checkbox" data-pos="0" checked="checked"> &nbsp;<input data-pos="0" class="textinput" maxlength="3" value="+2"> &nbsp;<span class="field">Force</span></div><div class="line line2">&nbsp;<input type="checkbox" data-pos="0" checked="checked"> &nbsp;<input data-pos="0" class="textinput" maxlength="3" value="+6"> &nbsp;<span class="field">Dextérité</span></div><div class="line line2">&nbsp;<input type="checkbox" data-pos="0"> &nbsp;<input data-pos="0" class="textinput" maxlength="3" value="+3"> &nbsp;<span class="field">Constitution</span></div><div class="line line2">&nbsp;<input type="checkbox" data-pos="0"> &nbsp;<input data-pos="0" class="textinput" maxlength="3" value="+0"> &nbsp;<span class="field">Intelligence</span></div><div class="line line2">&nbsp;<input type="checkbox" data-pos="0"> &nbsp;<input data-pos="0" class="textinput" maxlength="3" value="+2"> &nbsp;<span class="field">Sagesse</span></div><div class="line line2">&nbsp;<input type="checkbox" data-pos="0"> &nbsp;<input data-pos="0" class="textinput" maxlength="3" value="+0"> &nbsp;<span class="field">Charisme</span></div>					<div class="line line3">
                    <span class="field" style="width:100%">Jets de sauvegarde</span>
                </div>
            </div>
            <div class="block b16" style="width:100%">
            <div class="line">&nbsp;<input type="checkbox" data-pos="0" checked="checked"> &nbsp;<input data-pos="0" class="textinput" maxlength="3" value="+5"> &nbsp;<span class="field">Acrobaties (Dex)</span></div><div class="line">&nbsp;<input type="checkbox" data-pos="0"> &nbsp;<input data-pos="0" class="textinput" maxlength="3" value="+0"> &nbsp;<span class="field">Arcanes (Int)</span></div><div class="line">&nbsp;<input type="checkbox" data-pos="0" checked="checked"> &nbsp;<input data-pos="0" class="textinput" maxlength="3" value="+2"> &nbsp;<span class="field">Athlétisme (For)</span></div><div class="line">&nbsp;<input type="checkbox" data-pos="0"> &nbsp;<input data-pos="0" class="textinput" maxlength="3" value="+3"> &nbsp;<span class="field">Discrétion (Dex)</span></div><div class="line">&nbsp;<input type="checkbox" data-pos="0"> &nbsp;<input data-pos="0" class="textinput" maxlength="3" value="+2"> &nbsp;<span class="field">Dressage (Sag)</span></div><div class="line">&nbsp;<input type="checkbox" data-pos="0"> &nbsp;<input data-pos="0" class="textinput" maxlength="3" value="+3"> &nbsp;<span class="field">Escamotage (Dex)</span></div><div class="line">&nbsp;<input type="checkbox" data-pos="0"> &nbsp;<input data-pos="0" class="textinput" maxlength="3" value="+0"> &nbsp;<span class="field">Histoire (Int)</span></div><div class="line">&nbsp;<input type="checkbox" data-pos="0"> &nbsp;<input data-pos="0" class="textinput" maxlength="3" value="+0"> &nbsp;<span class="field">Intimidation (Cha)</span></div><div class="line">&nbsp;<input type="checkbox" data-pos="0" checked="checked"> &nbsp;<input data-pos="0" class="textinput" maxlength="3" value="+4"> &nbsp;<span class="field">Intuition (Sag)</span></div><div class="line">&nbsp;<input type="checkbox" data-pos="0"> &nbsp;<input data-pos="0" class="textinput" maxlength="3" value="+0"> &nbsp;<span class="field">Investigation (Int)</span></div><div class="line">&nbsp;<input type="checkbox" data-pos="0"> &nbsp;<input data-pos="0" class="textinput" maxlength="3" value="+2"> &nbsp;<span class="field">Médecine (Sag)</span></div><div class="line">&nbsp;<input type="checkbox" data-pos="0"> &nbsp;<input data-pos="0" class="textinput" maxlength="3" value="+0"> &nbsp;<span class="field">Nature (Int)</span></div><div class="line">&nbsp;<input type="checkbox" data-pos="0" checked="checked"> &nbsp;<input data-pos="0" class="textinput" maxlength="3" value="+4"> &nbsp;<span class="field">Perception (Sag)</span></div><div class="line">&nbsp;<input type="checkbox" data-pos="0" checked="checked"> &nbsp;<input data-pos="0" class="textinput" maxlength="3" value="+2"> &nbsp;<span class="field">Persuasion (Cha)</span></div><div class="line">&nbsp;<input type="checkbox" data-pos="0"> &nbsp;<input data-pos="0" class="textinput" maxlength="3" value="+0"> &nbsp;<span class="field">Religion (Int)</span></div><div class="line">&nbsp;<input type="checkbox" data-pos="0"> &nbsp;<input data-pos="0" class="textinput" maxlength="3" value="+0"> &nbsp;<span class="field">Représentation (Cha)</span></div><div class="line">&nbsp;<input type="checkbox" data-pos="0" checked="checked"> &nbsp;<input data-pos="0" class="textinput" maxlength="3" value="+4"> &nbsp;<span class="field">Survie (Sag)</span></div><div class="line">&nbsp;<input type="checkbox" data-pos="0"> &nbsp;<input data-pos="0" class="textinput" maxlength="3" value="+0"> &nbsp;<span class="field">Tromperie (Cha)</span></div>					<div class="line line2">
                    <span class="field" style="width:100%">Compétences</span>
                </div>
            </div>
            <div class="block b17">
                <div class="line line1">
                    <input data-pos="0" class="textinput" maxlength="2" style="width:17%" value="12">
                    <div class="field" style="width:83%">Sagesse (Perception) passive</div>
                </div>
            </div>
            <div class="block b20" style="width:100%">
                <div class="block b21" style="width:31%">
                    <input data-pos="0" class="textinput" maxlength="6" value="16">
                    <span class="field" style="width:100%"><br>CA</span>
                </div><div class="block b22" style="width:31%">
                    <input data-pos="0" class="textinput" maxlength="6" value="+3">
                    <br><span class="field" style="width:100%"><br>Initiative</span>
                </div><div class="block b23" style="width:31%">
                    <input data-pos="0" class="textinput" maxlength="6" value="12 m">
                    <br><span class="field" style="width:100%"><br>Vitesse</span>
                </div>
            </div><div class="block b24" style="width:100%">
                <div class="line line1">
                    <span class="field" style="width:60%">Maximum de points de vie</span>
                    <input data-pos="0" class="textinput" maxlength="8" style="width:40%" value="19">
                </div>
            </div>
        """.trimIndent()
    }
}
