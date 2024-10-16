Situation Partie 1 : 

Une salle de cinéma contient 50 places. 5 rangées numérotées de A à E, 10 colonnes numérotées de 1 à 10). 
Une interface permet de monitorer les réservations de places faites en ligne. 

L'interface graphique est constituée de boutons représentant les places. 
Quand une place est reservée le bouton correspondant prends la couleur rouge. 

L'opérateur de la salle peut utiliser l'interface pour visualiser qui a reservé une place en cliquant dessus.
Cela ouvre une nouvelle fenetre avec les informations du client. 
Si il recoit un mail de désistement, il lui est également possible de libérer la place en cliquant sur un bouton
"libérer la place" ce qui ouvre une petite fenetre de confirmation.

Afin de simuler les réservations des clients, un fichier .data vous est fourni. 
Celui ci représente les demandes de reservations (une reservation par ligne du fichier).
On considère que chaque seconde, une nouvelle demande de reservation est faite.

Pour qu'une demande de reservation soit valide, il faut que la place demandée ne soit pas déja reservée sinon, 
la nouvelle demande de reservation est ignorée. 
Pour chaque reservation le client entre son nom, prénom et mail et la place souhaitée.   
Dans le fichier cela correspond à par exemple : 
"John;Smith;jsmith@mail.com;A;5"

Situation Partie 2 : 

Après quelques semaines d'utilisation, on se rends compte que des erreurs arrivent plus ou moins 
fréquement dans les demandes de reservations. Par exemple, des demandes pour des sieges qui n'existent pas 
dans la salle ou des informations manquantes ou invalides peuvent survenir. Ces demandes invalides doivent 
être ignorées. 
 
Afin de s'assurer du comportement normal du système de reservation, on vous demande d'implémenter 
des tests unitaires permettant de garantir que des valeur incohérentes recues en entrée du programme 
ne comprometeront pas le comportement cohérent du systeme.

















