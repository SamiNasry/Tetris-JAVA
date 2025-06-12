# Tetris ENSAB

Ce projet est une implémentation complète du jeu **Tetris** réalisée dans le cadre du module Java à l'**École Nationale des Sciences Appliquées de Berrechid (ENSAB)**, sous la supervision de **Monsieur Lahcen Moumoune**.

## Présentation

Ce Tetris propose une interface graphique moderne, un système de score, la gestion du meilleur score (stocké dans une base de données SQLite), la prévisualisation de la prochaine pièce, la gestion du niveau, et des contrôles clavier intuitifs.  
Le projet met en avant la maîtrise de la programmation orientée objet en Java, la gestion d'événements, l'utilisation de Swing pour l'interface graphique, et l'intégration d'une base de données embarquée.

## Fonctionnalités

- Interface graphique conviviale (Java Swing)
- Affichage du score, du nombre de lignes, du niveau et du meilleur score
- Sauvegarde automatique du meilleur score dans une base SQLite
- Prévisualisation de la prochaine pièce
- Contrôles clavier personnalisés (voir ci-dessous)
- Pause, reprise et redémarrage rapide de la partie
- Bouton pour réinitialiser le meilleur score
- Affichage d'un logo ENSAB et d'une mention du module

## Contrôles

- **S** : Démarrer ou recommencer une partie
- **P** : Pause / Reprendre
- **Flèche gauche / A** : Déplacer la pièce à gauche
- **Flèche droite / D** : Déplacer la pièce à droite
- **Flèche bas / S** : Descente rapide
- **Flèche haut / W** : Rotation à droite
- **Z** : Rotation à gauche
- **Espace** : Chute instantanée de la pièce

## Installation et Lancement

1. **Prérequis** :
   - Java 17 ou supérieur
   - Maven

2. **Cloner le projet** :
   ```bash
   git clone <url-du-repo>
   cd Tetris-clone-project
   ```

3. **Placer le logo**  
   Vérifiez que le fichier `logo-ensa-berrechid.png` est bien dans `src/main/resources`.

4. **Compiler et lancer** :
   ```bash
   mvn clean package
   mvn exec:java -Dexec.mainClass="Tetris"
   ```

   Ou lancez le projet depuis votre IDE favori (IntelliJ, Eclipse...).

## Structure du projet

- `src/main/java/` : Code source Java (Tetris, Board, Shape, HighScoreManager, etc.)
- `src/main/resources/` : Ressources (logo ENSAB)
- `pom.xml` : Configuration Maven

## Auteurs

- Moussaif Fahd
- Nasry Sami
- Louaddi Zakaria  
- AIT LAADIK Soukaina

## Remerciements

Projet réalisé pour le module Java à l'**ENSAB**  
**Enseignant : Monsieur Lahcen MOUMOUN**

---


