package com.example.dndcombatroller

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dndcombatroller.data.LocalCombatRepository
import com.example.dndcombatroller.ui.CombatScreen
import com.example.dndcombatroller.ui.CombatViewModel
import com.example.dndcombatroller.ui.FichePersoScreen
import com.example.dndcombatroller.ui.theme.DnDCombatRollerTheme

private enum class Ecran { Combat, FichePerso }

@Composable
@Preview
fun App() {
    DnDCombatRollerTheme {
        val repository = LocalCombatRepository.current
        val viewModel: CombatViewModel = viewModel { CombatViewModel(repository) }
        val etat by viewModel.uiState.collectAsStateWithLifecycle()
        var ecranCourant by rememberSaveable { mutableStateOf(Ecran.Combat) }
        when (ecranCourant) {
            Ecran.Combat -> CombatScreen(
                onOuvrirFiche = { ecranCourant = Ecran.FichePerso },
            )
            Ecran.FichePerso -> FichePersoScreen(
                fiche = etat.fiche,
                pvActuel = etat.pvActuel,
                pvTemporaires = etat.pvTemporaires,
                onImporterFiche = viewModel::importerFiche,
                onAjusterPvActuel = viewModel::ajusterPvActuel,
                onAjusterPvTemporaires = viewModel::ajusterPvTemporaires,
                onRetour = { ecranCourant = Ecran.Combat },
            )
        }
    }
}
