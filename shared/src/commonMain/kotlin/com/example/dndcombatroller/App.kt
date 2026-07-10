package com.example.dndcombatroller

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.dndcombatroller.ui.CombatScreen
import com.example.dndcombatroller.ui.FichePersoScreen
import com.example.dndcombatroller.ui.theme.DnDCombatRollerTheme

private enum class Ecran { Combat, FichePerso }

@Composable
@Preview
fun App() {
    DnDCombatRollerTheme {
        var ecranCourant by rememberSaveable { mutableStateOf(Ecran.Combat) }
        when (ecranCourant) {
            Ecran.Combat -> CombatScreen(
                onOuvrirFiche = { ecranCourant = Ecran.FichePerso },
            )
            Ecran.FichePerso -> FichePersoScreen(
                onRetour = { ecranCourant = Ecran.Combat },
            )
        }
    }
}
