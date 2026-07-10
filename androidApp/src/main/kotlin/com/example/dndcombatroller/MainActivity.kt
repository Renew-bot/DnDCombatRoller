package com.example.dndcombatroller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import com.example.dndcombatroller.data.DataStoreCombatRepository
import com.example.dndcombatroller.data.LocalCombatRepository

class MainActivity : ComponentActivity() {

    private val combatRepository by lazy { DataStoreCombatRepository(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            CompositionLocalProvider(LocalCombatRepository provides combatRepository) {
                App()
            }
        }
    }
}
