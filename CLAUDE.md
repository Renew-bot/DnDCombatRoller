# DnD Combat Roller — Documentation pour Claude

## But de l'application

Application Android de gestion des jets de dés pour les combats Donjons & Dragons 5e.
Elle permet, en cours de partie, de configurer et lancer rapidement les jets d'attaque
et de dégâts de ses personnages ou monstres, sans quitter l'écran.

Orientation : paysage uniquement (`sensorLandscape`).

---

## Architecture

### Structure du projet

Projet **Kotlin Multiplatform (KMP)** généré par Android Studio.

```
DnDCombatRoller/
├── androidApp/          — entrée Android (MainActivity, Manifest, ressources)
├── shared/              — code partagé (Android cible principale)
│   └── src/commonMain/kotlin/com/example/dndcombatroller/
│       ├── domain/      — modèles métier + moteur de dés (0 dépendance Android)
│       ├── ui/          — composables Compose + ViewModel + state
│       └── data/        — CombatRepository (interface) + InMemoryCombatRepository
│   └── src/androidMain/kotlin/…/data/
│       └── DataStoreCombatRepository — implémentation DataStore (Android uniquement)
├── desktopApp/          — cible Desktop (secondaire)
├── webApp/              — cible Web (secondaire)
└── iosApp/              — cible iOS (secondaire)
```

### Pattern MVVM

```
CombatScreen (Composable)
    └── observe → CombatViewModel.uiState : StateFlow<CombatUiState>
                      └── appelle → MoteurDeDes (domain/engine)
                      └── lit/écrit → CombatRepository (data)
```

---

## Conventions

| Règle | Détail |
|---|---|
| Noms du domaine métier | **Français** : `Attaque`, `EtapeDeJet`, `TypeDe`, `MoteurDeDes` |
| Code technique | **Anglais** : `ViewModel`, `StateFlow`, `Repository`, packages, fonctions d'extension |
| Modèles sérialisables | Annotation `@Serializable` (kotlinx.serialization) |
| Pas de commentaires | Sauf invariant non-évident |
| Pas de logique dans les Composables | Tout dans le ViewModel via `uiState` |

---

## Dépendances clés

- **Compose Multiplatform 1.11.1** (JetBrains) — UI
- **Material 3** (`org.jetbrains.compose.material3`) — design system
- **lifecycle-viewmodel-compose** (JetBrains KMP) — ViewModel dans commonMain
- **kotlinx-coroutines-core 1.11.0** — StateFlow / suspend
- **kotlinx-serialization-json 1.8.1** — sérialisation des modèles métier
- **androidx.datastore:datastore-preferences 1.1.4** — persistance Android (androidMain seulement)
- **JUnit 4** — tests unitaires domain (sans Android)

---

## Persistance

`List<Attaque>` et `nomPersonnage` sont persistés entre sessions via **DataStore Preferences + kotlinx.serialization**.

- Choix DataStore plutôt que Room : données plates `@Serializable`, pas de jointures, pas de migrations.
- `CombatRepository` (interface commonMain) / `InMemoryCombatRepository` (no-op, cibles non-Android et tests) / `DataStoreCombatRepository` (androidMain).
- Injection via `CompositionLocalProvider(LocalCombatRepository provides …)` dans `MainActivity`.
- Le ViewModel lit le dépôt au `init` ; l'historique et le compteur de dégâts restent **en mémoire uniquement** (réinitialisés au lancement).
- `viewModelScope.launch` inaccessible en tests JVM (pas de `Dispatchers.Main`) → encapsulé dans `lancerSuspend { }` qui absorbe `IllegalStateException` silencieusement.

---

## Rotation / reconfiguration

`ecranCourant` (enum `Ecran : Serializable`) dans `App.kt` utilise `rememberSaveable` — survit aux rotations sans que le ViewModel ne soit recréé.

---

## Accessibilité

- `contentDescription` ajouté sur : bouton Fiche (BarreHaut), Annuler/Refaire (BarreBas), +/− modificateur (ZoneDes).
- `semantics { selected = … }` sur les cartes d'attaque (PanneauAttaques).
- Thème sombre : `DnDOr` (`0xFFFFD700`) sur fond `DnDNoir` (`0xFF1A1A2E`) — contraste correct pour les valeurs primaires.

---

## Zones de l'écran principal (paysage)

```
┌────────────────────────────────────────────────────────────────────┐
│  [Barre d'actions rapides]              [Nom du personnage / round] │
├─────────────────────────┬──────────────────────────────────────────┤
│                         │                                          │
│   Liste des Attaques    │        Zone de résultats des jets        │
│   (scroll vertical)     │        (historique + total)              │
│                         │                                          │
│   ┌─────────────────┐   │   ┌──────────────────────────────────┐  │
│   │  Attaque #1     │   │   │  Touche : 1d20+5 → 17 ✓         │  │
│   │  Attaque #2     │   │   │  Dégâts : 1d8+3 → 6              │  │
│   │  + Ajouter      │   │   │  ──────────────────────────────  │  │
│   └─────────────────┘   │   │  [historique des jets précédents] │  │
│                         │   └──────────────────────────────────┘  │
├─────────────────────────┴──────────────────────────────────────────┤
│              [Bouton LANCER / RELANCER]                             │
└────────────────────────────────────────────────────────────────────┘
```

### Zones nommées
- **PanneauAttaques** — liste configurable des `Attaque` du joueur
- **ZoneResultats** — affichage du détail du dernier jet + historique
- **BoutonLancer** — déclenche le jet via `CombatViewModel`
- **BarreActions** — actions globales (reset, configuration, personnage)
