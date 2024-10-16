# HELBThermo-Systeme-Thermodynamique
HELBThermo est une application développée en <b> Java </b> avec  <b> JavaFX </b>et intégrant des design patterns. Ce projet vise à simuler un système thermodynamique où des échanges de chaleur se produisent entre des cellules. L'application permet aux utilisateurs de configurer, personnaliser et observer ces échanges à travers une interface interactive.
# Fonctionnalités principales
* <b>Configuration Initiale</b> : L'utilisateur peut définir la taille du système, le nombre de sources de chaleur, leur température initiale, ainsi que la disposition de ces sources (en configuration "X" ou "+").
<div align="center">
  <img src="https://github.com/user-attachments/assets/56ab91d9-0a76-4bf3-96a9-4f5e957c1e3b" alt="Image du système de configuration" width="400">
</div>

* <b>Contrôle de la Simulation</b> : Boutons "Play", "Pause", et "Reset" pour contrôler la simulation.
* <b>Choix de différents modes de chauffe</b> (manuel, total, premier, pair, impair).
* <b>Ajustement de la vitesse de simulation</b> (normale, accélérée, ralentie).
 <div align="center"> <img src="https://github.com/user-attachments/assets/37280151-e890-4de2-80c2-ad6e82e927aa" alt="Image du système thermodynamique" width="400"> </div>

* <b>Affichage Dynamique</b> : Les cellules sont affichées avec des couleurs variant du bleu (températures froides) au rouge (températures chaudes). La température extérieure et la température moyenne des cellules actives sont également affichées.
* <b>Gestion des Cellules</b> : Possibilité d'activer ou désactiver des sources de chaleur, de transformer des cellules en cellules mortes, ou de définir une nouvelle température pour une source de chaleur existante.
  <div align="center"> <img src="https://github.com/user-attachments/assets/613dfac5-d78e-4d11-8794-7a37ba1c3064" alt="Image de gestion des cellules" width="400"> </div>
* <b>Fichier Log</b> : À la fermeture de l'application, un fichier log est créé avec l'état du système à un moment donné, incluant les informations sur les cellules, leur température, et leur statut.
* <b>Tests Unitaires</b> : Des tests unitaires ont été réalisés avec <b>JUnit5</b> pour garantir le bon fonctionnement de la simulation en cas de valeurs incohérentes.
# Design Patterns utilisés
* <b>MVC (Model-View-Controller)</b> : Séparation claire entre la logique métier (le modèle), l'interface utilisateur (la vue), et les interactions utilisateur (le contrôleur).
* <b>Observer</b> : Permet la communication entre le modèle et la vue, garantissant une mise à jour automatique des données.
* <b>Factory</b> : Utilisé pour créer différents types de cellules dans le système, facilitant ainsi la gestion et la modification du code.
* <b>Singleton</b> : Implémenté pour le système de logging, garantissant qu'une seule instance du logger est utilisée tout au long de l'application.
