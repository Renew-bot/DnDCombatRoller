package com.example.dndcombatroller.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.dndcombatroller.domain.model.Attaque
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore(name = "dnd_combat")

private val ATTAQUES = stringPreferencesKey("attaques")
private val NOM = stringPreferencesKey("nom_personnage")

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
}
