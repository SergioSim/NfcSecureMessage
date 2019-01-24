# NfcSecureMessage
Application qui permet de crypter les messages via le tag NFC
# Pré Requis
android sdk 23 ou plus recente,
telephone portable android ayant NFC allume et un acces a l'internet
# Installation
Il suffit importer le dossier NfcSecureMessage dans android studio comme un projet existant, lancer la synchronisation gradle, compiler et lancer le projet.
# Structure du projet
notre projet à 3 principaux composantes: application android situe dans le dossier NfcSecureMessage, un serveur node situe dans le dosser NodeServer et une base des donnes PhpMyAdmin. Nous  avons mis en ligne le serveur node et la base des donnes pour qu'il soit suffisant de lancer que l'application andoid et elle soit fonctionnelle.
# Structure detaille du projet Andoid
Notre application Android est decoupe en 6 packages:
ans/mbds - contient tous les vues et tout liee directement a l'interaction avec l'utilisateur
cryptoTools - contient des utils pour le cryptage et decrypdage
database - contient des utils pour la interaction avec la base des donnes sqlite
network - contient des utils pour faire des requettes reseau
nfctools - contient des utils pour la interaction avec des tags nfc
utils - contient des utils de support diverse utilises par la majorite des compostants
