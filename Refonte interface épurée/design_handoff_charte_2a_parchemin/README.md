# Handoff : Charte graphique "2a — Parchemin de campagne"

## Overview
Nouvelle charte graphique pour l'app DnD Combat Roller (Kotlin Multiplatform / Compose), appliquée à l'écran de combat. Remplace la charte actuelle (bleu nuit + violet/rose néon, dégradés, ombres, pilules) par une palette claire tons cuir/parchemin/or, avec des composants plus plats et plus petits.

## About the Design Files
Les fichiers de ce dossier sont des **références de design créées en HTML** (prototype statique) — elles montrent le rendu visuel voulu, ce n'est pas du code à copier tel quel. La tâche : recréer cette charte graphique dans l'app Kotlin Multiplatform / Jetpack Compose existante (`shared/src/commonMain/kotlin/com/example/dndcombatroller/ui/`), en réutilisant les composables déjà en place (`CombatScreen.kt`, `BarreHaut.kt`, `BarreBas.kt`, `PanneauAttaques.kt`, `ZoneDes.kt`, `Historique.kt`, `theme/Color.kt`, `theme/Theme.kt`) plutôt qu'en réécrivant l'app.

## Fidelity
**Haute-fidélité (hifi)** pour la palette de couleurs, les rayons de bordure et les tailles de police/composants. La disposition (layout) doit suivre celle déjà présente dans le repo Compose actuel (paysage : colonne latérale attaques/historique + zone de jet ; portrait : colonne unique) — ce mockup HTML illustre juste comment cette disposition existante doit être recolorée et resserrée.

## Design Tokens

### Couleurs
- Fond principal : `#F4ECDD` (parchemin clair)
- Fond des cartes/zones internes : `#FBF3E4`
- Bordures : `#D9CBB0`, épaisseur 1px
- Accent principal (boutons actifs, onglet sélectionné, bouton Lancer) : `#7A2E2E` (cuir/bordeaux)
- Texte principal : `#2B2016`
- Texte secondaire / labels : `#6B5D4B`
- Résultat de jet (chiffre mis en avant) : `#B8860B` (or)
- Dégâts du tour : `#4B6B4E` (vert sauge)
- Fond des boutons inactifs : `#FFFFFF` avec bordure `#D9CBB0`

### Typographie
- Police système (`-apple-system` / équivalent Compose : police par défaut du thème, pas de police custom)
- Résultat de jet : ~18px, weight 800
- Titres d'attaque sélectionnée / boutons clés : ~10px, weight 700 (à l'échelle mobile réelle : prévoir un palier ~2x plus grand, ce mockup est en miniature d'aperçu)
- Labels secondaires (« Toucher », « Prochain », etc.) : ~8px weight 400, même remarque d'échelle

### Rayons et espacements
- Cartes/zones internes : border-radius 8-9px
- Boutons pilule → **remplacés par rectangles arrondis** 6-8px (ne plus utiliser de pilules ni dégradés ni ombres portées fortes — seulement une ombre légère sur le contour extérieur de l'écran, `0 2px 8px rgba(43,32,22,.12)`)
- Boutons d'onglet (Attaques/Historique) : hauteur ~18-26px selon le contexte, radius 6-7px

### États
- Onglet/bouton actif : fond accent `#7A2E2E`, texte blanc
- Onglet/bouton inactif : fond blanc, bordure `#D9CBB0`, texte `#6B5D4B` ou `#2B2016`
- Bouton "Lancer" désactivé (aucune attaque sélectionnée) : opacité réduite, garder la forme

## Screens / Views

### 1. Écran de combat — paysage
- **Layout** : colonne gauche fixe (~130px de large dans le mockup, à l'échelle réelle ~230px) contenant : deux onglets Attaques/Historique côte à côte, bouton "+ Ajouter", liste des attaques (carte pleine = sélectionnée avec fond accent, carte bordée = non sélectionnée), bouton "LANCER" en bas.
- Colonne droite : bandeau haut (résultat du dernier jet à gauche dans une carte encadrée + nom du personnage et bouton "Fiche" à droite), zone centrale (carte "étape en cours" + carte "prochain jet" + carte "dégâts du tour"), bandeau bas (Annuler / Refaire / Fin du tour).
- Couleurs et rayons : voir Design Tokens ci-dessus.

### 2. Écran de combat — portrait / tablette
- Colonne unique empilée verticalement : bandeau nom personnage + bouton Fiche, rangée résultat de jet + dégâts du tour, carte étape en cours, onglets Attaques/Historique, liste (attaques ou historique selon l'onglet), barre du bas (Annuler / Refaire / Lancer).
- Mêmes tokens de couleur/rayon que la version paysage.

## Interactions & Behavior
Comportement inchangé par rapport à l'app actuelle — seule l'habillage visuel change :
- Sélection d'une attaque dans la liste → surlignée avec le fond accent.
- Bouton Lancer → déclenche le jet, affiche le résultat dans la carte "résultat".
- Annuler / Refaire → historique de jets (undo/redo).
- Fin du tour → remet à zéro le compteur de dégâts du tour (avec confirmation).
- Onglets Attaques ↔ Historique → bascule le contenu de la liste, pas de changement de route.

## Assets
Aucune image — uniquement des couleurs plates et du texte. Aucune icône custom requise (les flèches ↩/↪ peuvent rester des glyphes texte ou être remplacées par les icônes déjà utilisées dans l'app).

## Files
- `reference-mockup-toutes-variantes.html` — fichier de référence complet (ouvrir dans un navigateur). Chercher la section `id="2a"` (ancre `#2a`) : c'est la charte "Parchemin de campagne" à implémenter, montrée sur les deux layouts (paysage `1a` et portrait `1c`). Les autres sections du fichier (autres chartes, versions interactives) ne font pas partie de ce handoff et peuvent être ignorées.
