package com.example.dndcombatroller.data

import com.example.dndcombatroller.domain.model.Attaque
import com.example.dndcombatroller.domain.model.FichePersonnage
import com.example.dndcombatroller.domain.model.PointsDeVie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
private data class SaveData(
    val attaques: List<Attaque> = emptyList(),
    val nomPersonnage: String = "",
    val fiche: FichePersonnage? = null,
    val pointsDeVie: PointsDeVie = PointsDeVie(),
)

class JsonCombatRepository : CombatRepository {

    private val fichier = File(System.getProperty("user.home"), ".dndcombatroller/combat.json")
    private val mutex = Mutex()
    private var cache: SaveData? = null

    private fun lireCache(): SaveData {
        if (cache == null) {
            cache = if (fichier.exists()) {
                runCatching { Json.decodeFromString<SaveData>(fichier.readText()) }
                    .getOrDefault(SaveData())
            } else {
                SaveData()
            }
        }
        return cache!!
    }

    private fun ecrireCache(data: SaveData) {
        fichier.parentFile?.mkdirs()
        fichier.writeText(Json.encodeToString(data))
        cache = data
    }

    override suspend fun chargerAttaques(): List<Attaque> =
        withContext(Dispatchers.IO) { mutex.withLock { lireCache().attaques } }

    override suspend fun sauvegarderAttaques(attaques: List<Attaque>) =
        withContext(Dispatchers.IO) { mutex.withLock { ecrireCache(lireCache().copy(attaques = attaques)) } }

    override suspend fun chargerNomPersonnage(): String =
        withContext(Dispatchers.IO) { mutex.withLock { lireCache().nomPersonnage } }

    override suspend fun sauvegarderNomPersonnage(nom: String) =
        withContext(Dispatchers.IO) { mutex.withLock { ecrireCache(lireCache().copy(nomPersonnage = nom)) } }

    override suspend fun chargerFiche(): FichePersonnage? =
        withContext(Dispatchers.IO) { mutex.withLock { lireCache().fiche } }

    override suspend fun sauvegarderFiche(fiche: FichePersonnage) =
        withContext(Dispatchers.IO) { mutex.withLock { ecrireCache(lireCache().copy(fiche = fiche)) } }

    override suspend fun chargerPointsDeVie(): PointsDeVie =
        withContext(Dispatchers.IO) { mutex.withLock { lireCache().pointsDeVie } }

    override suspend fun sauvegarderPointsDeVie(pointsDeVie: PointsDeVie) =
        withContext(Dispatchers.IO) { mutex.withLock { ecrireCache(lireCache().copy(pointsDeVie = pointsDeVie)) } }
}
