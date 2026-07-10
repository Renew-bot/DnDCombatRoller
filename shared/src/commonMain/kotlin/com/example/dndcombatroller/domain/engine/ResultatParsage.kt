package com.example.dndcombatroller.domain.engine

sealed class ResultatParsage {
    data class GroupeDes(val nombre: Int, val faces: Int)
    data class Succes(val groupes: List<GroupeDes>, val modificateur: Int) : ResultatParsage()
    data class Erreur(val message: String) : ResultatParsage()
}
