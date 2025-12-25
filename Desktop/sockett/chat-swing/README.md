# ChatVoiceGUI - Explication du projet

## Description du projet
Ce projet est une application Java de chat vocal et textuel.
Il utilise :
- **Java** (programmation orientée objet)
- **Swing** pour l’interface graphique
- **Sockets** pour la communication client-serveur

L’objectif est de permettre à plusieurs clients de communiquer
entre eux via une interface graphique conviviale.

---

## Structure du projet

### Dossiers principaux

- `src/`
    - `client/` : classes côté client (ChatClient, ChatVoiceGUI, VoiceUtils)
    - `server/` : classes côté serveur (ChatServer, ClientHandler)
- `bin/` : fichiers compilés (.class)
- `.vscode/` : configuration de VS Code
- `lib/` : bibliothèques externes (si utilisées)

---

## Explication du code

### Serveur (`ChatServer.java`)
- Écoute les connexions des clients via un port réseau
- Crée un **ClientHandler** pour chaque client connecté
- Reçoit et envoie les messages aux clients

### Client (`ChatClient.java`)
- Se connecte au serveur via une socket
- Envoie les messages saisis par l’utilisateur
- Reçoit les messages d’autres clients

### Interface graphique (`ChatVoiceGUI.java`)
- Affiche une fenêtre Swing pour le chat
- Contient :
    - zone de texte pour afficher les messages
    - champ de saisie pour envoyer un message
    - boutons pour actions supplémentaires

### Gestion de l’audio (`VoiceUtils.java`)
- Contient les fonctions pour gérer l’envoi et la réception de messages vocaux
- Encode et décode le son pour transmission via le réseau

---

## Compilation et exécution
1. Compiler les fichiers Java (`src/`) → fichiers `.class` générés dans `bin/`
2. Lancer le serveur : `java server.ChatServer`
3. Lancer un ou plusieurs clients : `java client.ChatClient`

---

## Notes
- Ce projet illustre la **POO**, la **programmation réseau** et l’**interface graphique Swing**
- Les fichiers `.class` **ne doivent pas être modifiés**
- Ce README sert uniquement à expliquer le fonctionnement du code
