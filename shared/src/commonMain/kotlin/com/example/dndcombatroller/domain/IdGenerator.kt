package com.example.dndcombatroller.domain

import kotlin.random.Random

fun generateId(): String = buildString {
    val hex = "0123456789abcdef"
    for (c in "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx") {
        append(
            when (c) {
                'x' -> hex[Random.nextInt(16)]
                'y' -> hex[Random.nextInt(4) + 8]
                else -> c
            }
        )
    }
}
