package com.example.dndcombatroller

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.dndcombatroller.data.DataStoreCombatRepository
import com.example.dndcombatroller.data.LocalCombatRepository
import com.example.dndcombatroller.data.LocalSelecteurFichier
import com.example.dndcombatroller.data.SelecteurFichier

class MainActivity : ComponentActivity() {

    private val combatRepository by lazy { DataStoreCombatRepository(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            var onTexteCharge by remember { mutableStateOf<((String) -> Unit)?>(null) }
            val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                val callback = onTexteCharge
                if (uri != null && callback != null) {
                    val texte = contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                    if (texte != null) callback(texte)
                }
            }
            val selecteurFichier = remember {
                SelecteurFichier { onTexte ->
                    onTexteCharge = onTexte
                    launcher.launch("*/*")
                }
            }
            CompositionLocalProvider(
                LocalCombatRepository provides combatRepository,
                LocalSelecteurFichier provides selecteurFichier,
            ) {
                App()
            }
        }
    }
}
