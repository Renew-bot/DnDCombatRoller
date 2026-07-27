package com.example.dndcombatroller.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.dndcombatroller.domain.model.Attaque
import com.example.dndcombatroller.domain.model.FichePersonnage
import com.example.dndcombatroller.domain.model.PointsDeVie
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore(name = "dnd_combat")

private val ATTAQUES = stringPreferencesKey("attaques")
private val NOM = stringPreferencesKey("nom_personnage")
private val FICHE = stringPreferencesKey("fiche_personnage")
private val POINTS_DE_VIE = stringPreferencesKey("points_de_vie")

class DataStoreCombatRepository(private val context: Context) : CombatRepository {

    override suspend fun chargerAttaques(): List<Attaque> {
        val json = context.dataStore.data.first()[ATTAQUES] ?: return emptyList()
        return runCatching { Json.decodeFromString<List<Attaque>>(json) }.getOrDefault(emptyList())
    }

    override suspend fun sauvegarderAttaques(attaques: List<Attaque>) {
        context.dataStore.edit { prefs -> prefs[ATTAQUES] = Json.encodeToString(attaques) }
    }

    override suspend fun chargerNomPersonnage(): String =
        context.dataStore.data.first()[NOM] ?: ""

    override suspend fun sauvegarderNomPersonnage(nom: String) {
        context.dataStore.edit { prefs -> prefs[NOM] = nom }
    }

    override suspend fun chargerFiche(): FichePersonnage? {
        val json = context.dataStore.data.first()[FICHE] ?: return null
        return runCatching { Json.decodeFromString<FichePersonnage>(json) }.getOrNull()
    }

    override suspend fun sauvegarderFiche(fiche: FichePersonnage) {
        context.dataStore.edit { prefs -> prefs[FICHE] = Json.encodeToString(fiche) }
    }

    override suspend fun chargerPointsDeVie(): PointsDeVie {
        val json = context.dataStore.data.first()[POINTS_DE_VIE] ?: return PointsDeVie()
        return runCatching { Json.decodeFromString<PointsDeVie>(json) }.getOrDefault(PointsDeVie())
    }

    override suspend fun sauvegarderPointsDeVie(pointsDeVie: PointsDeVie) {
        context.dataStore.edit { prefs -> prefs[POINTS_DE_VIE] = Json.encodeToString(pointsDeVie) }
    }
}
