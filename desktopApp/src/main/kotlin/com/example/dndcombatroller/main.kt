package com.example.dndcombatroller

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.example.dndcombatroller.data.JsonCombatRepository
import com.example.dndcombatroller.data.LocalCombatRepository

fun main() = application {
    val repository = remember { JsonCombatRepository() }
    Window(
        onCloseRequest = ::exitApplication,
        title = "DnDCombatRoller",
    ) {
        CompositionLocalProvider(LocalCombatRepository provides repository) {
            App()
        }
    }
}