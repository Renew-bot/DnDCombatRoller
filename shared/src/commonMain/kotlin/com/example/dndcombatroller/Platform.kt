package com.example.dndcombatroller

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform