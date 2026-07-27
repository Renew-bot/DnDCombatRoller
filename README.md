# DnD Combat Roller

Application Android (Kotlin Multiplatform) pour gérer rapidement les jets de dés
pendant les combats de Donjons & Dragons 5e, sans quitter la table de jeu.

---

## 🇫🇷 Français

### Présentation

DnD Combat Roller permet, en cours de partie, de configurer à l'avance les attaques
d'un personnage ou d'un monstre (jet d'attaque, jet de dégâts, notes libres) puis de
les lancer en un ou deux appuis pendant le combat. L'application est pensée pour être
utilisée en **paysage**, à côté de la table, sans manipulation superflue.

### Fonctionnalités

- **Bibliothèque d'attaques réutilisables** : chaque attaque est composée d'une ou
  plusieurs étapes (jet d'attaque, jet de dégâts, ou texte libre pour les effets
  spéciaux), avec expression de dés libre (`1d20+4`, `2d6+1d8+3`, etc.).
- **Lancer en un geste** : sélection d'une attaque puis bouton **Lancer**, avec
  animation et détail des dés obtenus.
- **Avantage / Désavantage** : bascule à 3 positions (Avantage / Normal / Désavantage),
  active automatiquement sur les jets 1d20 uniques ; le dé écarté est affiché barré.
- **Jets naturels mis en évidence** : un **20 naturel** (or) ou un **1 naturel**
  (rouge) sur un d20 unique est signalé dans le résultat en haut d'écran et dans
  l'historique.
- **Modificateur ponctuel** : boutons `−` / `+` pour ajuster le jet en cours sans
  toucher à la configuration de l'attaque (ex. bonus temporaire, inspiration).
- **Compteur de dégâts du tour** : cumul automatique des dégâts infligés, remis à
  zéro à la fin du tour.
- **Historique de session** : journal chronologique de tous les jets effectués
  (dés lancés, modificateurs, total), avec défilement automatique.
- **Annuler / Refaire** : pile d'actions pour revenir en arrière ou rejouer une
  action annulée.
- **Fin du tour** : réinitialise la sélection et le compteur de dégâts, avec
  confirmation.
- **Gestion des attaques** : création, modification, suppression et
  réorganisation par glisser-déposer (appui long) dans le panneau des attaques.
- **Nom du personnage** : éditable directement depuis l'écran de combat.
- **Fiche personnage importée depuis aidedd.org** : CA, initiative, vitesse, PV
  max, perception passive, caractéristiques, jets de sauvegarde et compétences
  (maîtrise indiquée par un point de couleur) — voir
  [Importer sa fiche de personnage](#importer-sa-fiche-de-personnage) ci-dessous.
- **Suivi des PV et PV temporaires** : barre de vie avec boutons `−` / `+` (montant
  ajustable) pour les PV normaux et les PV temporaires. Les PV temporaires
  restent toujours visibles sur la barre, peuvent dépasser le total de PV, et
  sont retirés en priorité sur les dégâts.
- **Persistance locale** : la liste des attaques, le nom du personnage, la fiche
  importée et les PV/PV temporaires sont sauvegardés entre les sessions (dégâts
  du tour et historique remis à zéro à chaque lancement, volontairement).
- **Mise en page adaptative** : disposition dédiée en portrait et en paysage.

### Importer sa fiche de personnage

L'écran **Fiche**, accessible depuis le bouton *Fiche* de l'écran de combat, lit le
fichier HTML exporté par [aidedd.org](https://www.aidedd.org) (le générateur de
fiche de personnage francophone).

1. Sur aidedd.org, ouvrez votre fiche de personnage puis cliquez sur **Imprimer**
   pour afficher la version imprimable.
2. Depuis cette page imprimable, faites **Ctrl+S** (⌘+S sur Mac) pour
   l'enregistrer sur votre appareil au format **page HTML**.
3. Dans l'application, ouvrez le menu **Fiche**, appuyez sur **Importer**, puis
   sélectionnez le fichier HTML enregistré à l'étape précédente.

> ⚠️ Seul le format d'export d'aidedd.org est reconnu. Réimporter une fiche
> réinitialise les PV actuels au maximum et les PV temporaires à zéro.

### Architecture

Projet **Kotlin Multiplatform (KMP)**, cible principale Android, avec cibles
secondaires Desktop, Web et iOS pour partager l'UI en Compose Multiplatform.

```
DnDCombatRoller/
├── androidApp/          — entrée Android (MainActivity, Manifest, ressources)
├── shared/               — code partagé
│   └── src/commonMain/kotlin/com/example/dndcombatroller/
│       ├── domain/       — modèles métier + moteur de dés + parseur de fiche (0 dépendance Android)
│       ├── ui/           — composables Compose + ViewModel + state
│       └── data/         — CombatRepository (interface) + InMemoryCombatRepository
│   └── src/androidMain/kotlin/…/data/
│       └── DataStoreCombatRepository — implémentation DataStore (Android uniquement)
├── desktopApp/           — cible Desktop (secondaire)
├── webApp/                — cible Web (secondaire)
└── iosApp/                — cible iOS (secondaire)
```

Pattern **MVVM** :

```
CombatScreen / FichePersoScreen (Composable)
    └── observe → CombatViewModel.uiState : StateFlow<EtatCombat>
                      └── appelle → MoteurDeDes (domain/engine)
                      └── appelle → FichePersonnageParseur (domain/engine)
                      └── lit/écrit → CombatRepository (data)
```

### Conventions du code

| Règle | Détail |
|---|---|
| Noms du domaine métier | **Français** : `Attaque`, `EtapeDeJet`, `TypeDe`, `MoteurDeDes`, `FichePersonnage` |
| Code technique | **Anglais** : `ViewModel`, `StateFlow`, `Repository`, packages |
| Modèles sérialisables | Annotation `@Serializable` (kotlinx.serialization) |
| Composables | Aucune logique métier — tout passe par le ViewModel via `uiState` |

### Stack technique

- **Compose Multiplatform 1.11.1** (JetBrains) — UI
- **Material 3** — design system
- **lifecycle-viewmodel-compose** — ViewModel dans `commonMain`
- **kotlinx-coroutines-core 1.11.0** — `StateFlow` / suspend
- **kotlinx-serialization-json 1.8.1** — sérialisation des modèles métier
- **androidx.datastore:datastore-preferences 1.1.4** — persistance Android
- **JUnit 4** — tests unitaires du domaine (sans dépendance Android)

### Lancer l'application

Utilisez les configurations de lancement de votre IDE, ou en ligne de commande :

- Android : `./gradlew :androidApp:assembleDebug`
- Desktop :
  - Avec hot reload : `./gradlew :desktopApp:hotRun --auto`
  - Standard : `./gradlew :desktopApp:run`
- Web :
  - Cible Wasm (plus rapide, navigateurs récents) : `./gradlew :webApp:wasmJsBrowserDevelopmentRun`
  - Cible JS (plus lente, compatible anciens navigateurs) : `./gradlew :webApp:jsBrowserDevelopmentRun`
- iOS : ouvrir le dossier [`/iosApp`](./iosApp) dans Xcode et lancer depuis là.

### Lancer les tests

- Android : `./gradlew :shared:testAndroidHostTest`
- Desktop : `./gradlew :shared:jvmTest`
- Web :
  - Wasm : `./gradlew :shared:wasmJsTest`
  - JS : `./gradlew :shared:jsTest`
- iOS : `./gradlew :shared:iosSimulatorArm64Test`

### Builds & releases

Le workflow GitHub Actions **Build & Release** (`.github/workflows/build-release.yml`)
compile un `.apk` (Android), un `.deb` (Linux), un `.dmg` (macOS), un `.msi`
(Windows) et une archive web, et publie une release GitHub dès qu'un tag
`vX.Y.Z` est poussé (ou manuellement via `workflow_dispatch`).

### Limites connues / à venir

- Seul le format d'export **aidedd.org** est reconnu par l'import de fiche
  personnage ; les autres générateurs de fiche (D&D Beyond, etc.) ne sont pas
  pris en charge pour l'instant.
- L'**historique** et le **compteur de dégâts** du tour ne sont pas persistés :
  ils sont réinitialisés à chaque lancement de l'application.
- Pas de confirmation avant la suppression d'une attaque.
- Orientation **paysage** privilégiée (`sensorLandscape`) ; le mode portrait
  existe mais avec une disposition simplifiée.

---

## 🇬🇧 English

### Overview

DnD Combat Roller lets you pre-configure a character's or monster's attacks
(attack roll, damage roll, free-form notes) before a Dungeons & Dragons 5e
session, then roll them with one or two taps during combat. The app is
designed for **landscape** use at the table, with minimal friction between
turns.

### Features

- **Reusable attack library**: each attack is made of one or more steps
  (attack roll, damage roll, or free text for special effects), with a free
  dice expression (`1d20+4`, `2d6+1d8+3`, etc.).
- **One-tap rolling**: select an attack, tap **Roll**, and get an animated
  result with the full dice breakdown.
- **Advantage / Disadvantage**: a 3-way toggle (Advantage / Normal /
  Disadvantage), auto-enabled for single-d20 rolls; the discarded die is shown
  struck through.
- **Highlighted natural rolls**: a **natural 20** (gold) or **natural 1** (red)
  on a single d20 is called out in both the top result card and the history.
- **Ad-hoc modifier**: `−` / `+` buttons to tweak the current roll without
  editing the attack itself (e.g. temporary bonus, inspiration).
- **Turn damage counter**: automatically sums damage dealt during the current
  turn, reset when the turn ends.
- **Session history**: chronological log of every roll made (dice, modifiers,
  total), auto-scrolling to the latest entry.
- **Undo / Redo**: an action stack to step back or replay an undone action.
- **End of turn**: resets the current selection and the damage counter, behind
  a confirmation dialog.
- **Attack management**: create, edit, delete, and reorder attacks via
  long-press drag & drop in the attack panel.
- **Character name**: editable directly from the combat screen.
- **Character sheet imported from aidedd.org**: AC, initiative, speed, max HP,
  passive perception, ability scores, saving throws, and skills (proficiency
  shown as a colored dot) — see
  [Importing your character sheet](#importing-your-character-sheet) below.
- **HP and temporary HP tracking**: a health bar with `−` / `+` buttons (with
  an adjustable step amount) for both normal and temporary HP. Temporary HP
  always stays visible on the bar, can exceed the HP total, and is depleted
  first when damage is applied.
- **Local persistence**: the attack list, character name, imported sheet, and
  HP/temporary HP are saved between sessions (turn damage counter and history
  are intentionally reset on every launch).
- **Adaptive layout**: dedicated layouts for portrait and landscape.

### Importing your character sheet

The **Fiche** (character sheet) screen, reachable from the *Fiche* button on the
combat screen, reads the HTML file exported by
[aidedd.org](https://www.aidedd.org) (the French-language character sheet
builder).

1. On aidedd.org, open your character sheet and click **Imprimer** (Print) to
   open the printable version.
2. From that printable page, press **Ctrl+S** (⌘+S on Mac) to save it to your
   device as an **HTML page**.
3. In the app, open the **Fiche** menu, tap **Importer**, and pick the HTML
   file you just saved.

> ⚠️ Only aidedd.org's export format is recognized. Re-importing a sheet resets
> current HP to max and temporary HP to zero.

### Architecture

A **Kotlin Multiplatform (KMP)** project, targeting Android primarily, with
secondary Desktop, Web, and iOS targets sharing the Compose Multiplatform UI.

```
DnDCombatRoller/
├── androidApp/          — Android entry point (MainActivity, Manifest, resources)
├── shared/               — shared code
│   └── src/commonMain/kotlin/com/example/dndcombatroller/
│       ├── domain/       — business models + dice engine + sheet parser (no Android dependency)
│       ├── ui/           — Compose composables + ViewModel + state
│       └── data/         — CombatRepository (interface) + InMemoryCombatRepository
│   └── src/androidMain/kotlin/…/data/
│       └── DataStoreCombatRepository — DataStore implementation (Android only)
├── desktopApp/           — Desktop target (secondary)
├── webApp/                — Web target (secondary)
└── iosApp/                — iOS target (secondary)
```

**MVVM** pattern:

```
CombatScreen / FichePersoScreen (Composable)
    └── observes → CombatViewModel.uiState : StateFlow<EtatCombat>
                      └── calls → MoteurDeDes (domain/engine, dice engine)
                      └── calls → FichePersonnageParseur (domain/engine, sheet parser)
                      └── reads/writes → CombatRepository (data)
```

### Code conventions

| Rule | Detail |
|---|---|
| Domain names | **French** for business concepts: `Attaque`, `EtapeDeJet`, `TypeDe`, `MoteurDeDes`, `FichePersonnage` |
| Technical code | **English**: `ViewModel`, `StateFlow`, `Repository`, packages |
| Serializable models | `@Serializable` annotation (kotlinx.serialization) |
| Composables | No business logic — everything flows through the ViewModel via `uiState` |

### Tech stack

- **Compose Multiplatform 1.11.1** (JetBrains) — UI
- **Material 3** — design system
- **lifecycle-viewmodel-compose** — ViewModel in `commonMain`
- **kotlinx-coroutines-core 1.11.0** — `StateFlow` / suspend functions
- **kotlinx-serialization-json 1.8.1** — business model serialization
- **androidx.datastore:datastore-preferences 1.1.4** — Android persistence
- **JUnit 4** — domain unit tests (no Android dependency)

### Running the app

Use the run configurations provided by your IDE's toolbar, or these commands:

- Android app: `./gradlew :androidApp:assembleDebug`
- Desktop app:
  - Hot reload: `./gradlew :desktopApp:hotRun --auto`
  - Standard run: `./gradlew :desktopApp:run`
- Web app:
  - Wasm target (faster, modern browsers): `./gradlew :webApp:wasmJsBrowserDevelopmentRun`
  - JS target (slower, supports older browsers): `./gradlew :webApp:jsBrowserDevelopmentRun`
- iOS app: open the [`/iosApp`](./iosApp) directory in Xcode and run it from there.

### Running tests

- Android tests: `./gradlew :shared:testAndroidHostTest`
- Desktop tests: `./gradlew :shared:jvmTest`
- Web tests:
  - Wasm target: `./gradlew :shared:wasmJsTest`
  - JS target: `./gradlew :shared:jsTest`
- iOS tests: `./gradlew :shared:iosSimulatorArm64Test`

### Builds & releases

The **Build & Release** GitHub Actions workflow
(`.github/workflows/build-release.yml`) builds an `.apk` (Android), a `.deb`
(Linux), a `.dmg` (macOS), an `.msi` (Windows), and a web archive, and
publishes a GitHub release whenever a `vX.Y.Z` tag is pushed (or manually via
`workflow_dispatch`).

### Known limitations / roadmap

- Only the **aidedd.org** export format is recognized by the character sheet
  importer; other sheet builders (D&D Beyond, etc.) aren't supported yet.
- **History** and the turn **damage counter** are not persisted: both reset on
  every app launch.
- No confirmation prompt before deleting an attack.
- **Landscape** orientation is the primary target (`sensorLandscape`);
  portrait mode exists but uses a simplified layout.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)
and [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/#compose-multiplatform).
