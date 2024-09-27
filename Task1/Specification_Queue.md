### Specification pour la TASK2

# AVANT DE COMMENCER
Avant de commercer, il faut mieux avoir lu la spécification de la Task1 qui se trouve sur la branche Task1 de ce depot git. Dans cette branche, vous retrouverez : la specification, le design, les tests, et l'implementation de la première Task.

C'est à dire qu'ici, nous partons du principe que vous avez ce que c'est un broker et un channel. Que vous avez compris pourquoi avoir un brokerManagement, à quoi sert le rendez vous et les fonctionnalités d'un Task. 

# Nouvelle specification 
Dans la Task1 : deux tasks s'envoyaient des messages en suivant le protocole TCP. Les messages étaient sûrs d'arrivés (pas d'un coup certes) et dans le bon ordre. Cette fois, on va utiliser un protocole UDP. Les messages vont arriver sous forme de paquet.

Ici, nous avons QueueBroker qui se comporte comme Broker dans la Task1. Il est là afin de faire un accept et un connect entre deux Task et MessageQueue (comme le channel dans la Task1) sert de cannal de communique entre les deux Task pour qu'ils puissent s'envoyer ou recevoir des messages sous forme de paquet de byte.

## Connexion
Pour la Task2, la connexion fonctionne de la même manière que pour la Task1. Un Task réalise soit un accept ou un connect à un autre Task à partir du port et du name et attend que l'autre Task le rejoigne.

## Methode send 

Pour le méthode send : void send(byte[] bytes, int offset, int length);
- byte[] bytes : le tableau de byte qui va être écrit
- offset : l'indice de départ 
- length : la longueur du tableau

On va alors écrire dans le tableau de byte le message que nous voulons envoyer. C'est pour ça que nous ne retournons rien.

## Methode receive 

Pour le méthode receive : byte[] receive();
- byte[] : le tableau de byte que nous avons reçu

Nous allons retourner le tableau de bytes contenant ce que nous avons lu précédement.

## Methode close

Pour la déconnexion, c'est pareil que pour la Task1, un des deux Task se déconnecte et cela, rompt la connexion. L'autre Task sera alors notifie par une exception qui lui permettra de savoir que la connexion a été rompu.
