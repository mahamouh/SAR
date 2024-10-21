### Specification Only Event Queue

# AVANT DE COMMENCER
Avant de commencer, nous partons du principe que vous avez pris conscience de la specification de mixed event avec les queueBrokerEvent, les messageEvent, la EventPump. 

# Nouvelle Specification
Avant pour la task3, l'implémentation était un mixte event threadé qui utilisait une EventPump (qui contenait des eventTask) mais aussi des thread (telle que pour les fonctions bind, connect, receive et send).

Maintenant, nous allons utiliser seulement de l'event, en reécrivant nos classes Broker, Channel, QueueBroker, MessageQueue, pour n'avoir que des classes qui implementent un code full event.


## Broker 
Tout d'abord, les changements sur la classe Broker. Comme vous le savez, la classe Broker (des précédents tasks) contient les méthodes accept et connect afin de pouvoir créer un canal de communication entre un serveur et un client. Sauf que pour cela, nous avions un des deux (que ce soit le client ou le serveur) qui venait initier un Rdv et attendre que l'autre arrive pour répondre au Rdv. Cela générait les deux Channel que le serveur et le client afin besoin afin de communiquer. 

À present, notre classe Broker va alors avoir des listeners afin de le notifer quand un bind/unbind/connect a été realisé. Pour cela nous avons besoin de deux listener : AcceptBrokerListener et un ConnectBrokerListener : 

- AcceptBrokerListener : est là afin de notifier au task que son bind a bien eu lu
- ConnectBrokerListener : est là afin de notifier au task que son connect s'est bien passé

On crée des méthodes AcceptBrokerListener et ConnectBrokerListener afin de pouvoir faire la suite de la connexion sans être bloqué. 

Comment utiliser ces interfaces ? 

# AcceptBrokerListener
Broker a une méthode bind qui est : ``` boolean bind (int port, AcceptBrokerListener listener) ``` : 
- port : le port où la task est prêt à faire un accept
- listener : le listener qui va le notifier dès qu'un autre task peut se connecter à lui et réaliser une conenxion.

Si tout se passe bien, le listener va appeler sa méthode ``` void accepted(Channel channel) ``` une connexion sera faite. On peut aussi avoir une broker qui n'est plus prêt à faire des accept sur ce port et appelle la fonction unblind : ``` boolean unbind (int port) ``` pour dire qu'une connexion n'est possible.

# ConnectBrokerListener
Broker a une méthode connect : ``` boolean connect(String name, int port, ConnectBrokerListener listener) ``` : 
- name : le nom du Broker avec on veut se connecter
- port : le port où la task veut se connecter
- listener : le listener qui va le notifier dès qu'une autre task (qui a fait bind) est libre et peut se connecter.

Si tout se passe bien, le listener va appeler sa méthode ``` void connected(Channel channel) ``` et une connexion sera faite sinon la fonction refused : ``` void refused() ``` sera appelé pour dire qu'une connexion n'est pas possible/n'aura pas lieu sur ce port.

## QueueBroker 
QueueBroker est une classe abstraite utilisée pour gérer les connexions réseau et la communication via des files de messages (MessageQueue). Elle permet à un client de se connecter à un serveur ou à un serveur d'accepter des connexions entrantes.

Pour QueueBroker, nous aurons deux interfaces listener : un AcceptListener et un ConnectListener

- AcceptListener : est là afin de notifier au task que son bind a bien eu lu
- ConnectListener : est là afin de notifier au task que son connect s'est bien passé

On crée des méthodes AcceptListener et ConnectListener afin de pouvoir faire la suite de la connexion sans être bloqué. 

Comment utiliser ces interfaces ? 

# AcceptListener
QueueBroker a une méthode bind qui est : ``` boolean bind (int port, AcceptListener listener) ``` : 
- port : le port où la task est prêt à faire un accept
- listener : le listener qui va le notifier dès qu'un autre task peut se connecter à lui et réaliser une conenxion.

Si tout se passe bien, le listener va appeler sa méthode ``` void accepted(MessageQueue messageQueue) ``` une connexion sera faite. On peut aussi avoir une queueBorker qui n'est plus prêt à faire des accept sur ce port et appelle la fonction unblind : ``` boolean unbind (int port) ``` pour dire qu'une connexion n'est possible.

# ConnectListener
QueueBroker a une méthode connect : ``` boolean connect(String name, int port, ConnectionListener listener) ``` : 
- name : le nom du Broker avec on veut se connecter
- port : le port où la task veut se connecter
- listener : le listener qui va le notifier dès qu'une autre task (qui a fait bind) est libre et peut se connecter.

Si tout se passe bien, le listener va appeler sa méthode ``` void connected(MessageQueue messageQueue) ``` et une connexion sera faite sinon la fonction refused : ``` void refused() ``` sera appelé pour dire qu'une connexion n'est pas possible/n'aura pas lieu sur ce port.


# NB : 
Si une task essaie de se connecter alors qu'il n'y a pas de accept (bind) sur ce port, il se prend un refused

## MessageQueue
Pour MessageQueue, nous aurons une interface listener : Listener

- Listener : est là afin de notifier la task quand il pourra lire les messages qu'une autre task lui a envoyé ou écrire ou se fermer

Comment utiliser cette interfaces ? 

# Send 
MessageQueue a deux méthodes send 

- ``` boolean send(Message msg) ``` : pour envoyer le msg et return true si la task accept l'envoie
- ``` boolean send(Message msg) ``` : pour envoyer le msg et écrire une longueue len et return true si la task accept l'envoie

Des qu'un send a été fini, nous allons utiliser utiliser la méthode ``` void received(byte[] msg) ``` de ce listener.


# Close et closed
Pour ce qui est de la déconnexion, une task appelle sa méthode ``` void close() ```  et le listener notifie grâce au méthode 
``` void closed()``` que la connexion a été coupée.


## Channel 
Pour channel, nous aurons une interface : ListenerChannel 
- ListenerChannel : est là afin de notifier la task quand il pourra lire les messages qu'une autre task lui a envoyé ou écrire ou se fermer

# Write 
``` int write(byte[] bytes, int offset, int length) ``` 
- bytes : le tableau de bytes où il y'a les bytes à lire
- offset : l'indice de depart
- len : la longueur à lire
- return : le nombre de bytes écrites
Donc il faut que le tableu accepte la longueur offset et offset+len entre le départ et la fin.
Des qu'un write a été fini, nous allons utiliser utiliser la méthode ``` void wrote(Message msg) ``` de ce ListenerChannel.

# Read 
``` int read(byte[] bytes, int offset, int length)``` 
- bytes : le tableau de bytes où il y'a les bytes à lire
- offset : l'indice de depart
- len : la longueur à lire
- return : le nombre de bytes lus
Donc il faut que le tableu accepte la longueur offset et offset+len entre le départ et la fin.

Des qu'un write a été fini, nous allons utiliser utiliser la méthode ``` void read(Message msg) ``` de ce ListenerChannel.

# Disconnected et Disconnect
Pour ce qui est de la déconnexion, une task appelle sa méthode ``` void Disconnect() ```  et le listener notifie grâce au méthode 
``` void Disconnected()``` que la connexion a été coupée.

## Message 
Message prend un champ : un tableau de byte, un int offset et une len afin de tomber le bon tableau de byte qui doit être renvoyé.

## Task 
La classe Task permet de gérer l'exécution asynchrone de tâches. Elle fournit des méthodes pour ajouter des tâches à exécuter, arrêter ces tâches et vérifier leur état.

Méthodes
- ``` void post(Runnable r) ``` : permet d'ajouter une tâche (représentée par un Runnable) pour exécution asynchrone.
- ``` static Task task()``` : Renvoie l'instance de la tâche courante.
- ``` void kill() ``` : Arrête la tâche en cours, empêchant son exécution future.
- ``` boolean killed() ``` : Indique si la tâche a été arrêtée.

## EventPump 
EventPump est une pompe à event qui aura comme champ ``` List<Runnable> queue ```. Chaque Task va mette son runnable grâce à la méthode ``` void post(Runnable r) ``` et alors crée une pompe avec plusieurs événements (avec les autres Task).
Ici, EventPump extends Thread ce qui veut dire qu'il a une méthode run() qui va être fait. Dans notre ças, on a un ``` public synchronized void run() ``` où on fait le run de tous les runnables se trouvant dans notre liste de queue.
Nous avons aussi : 
- Une méthode  ``` public synchronized void post(Runnable r) ``` afin d'ajouter des runnable à notre pompe à events
- Une méthode ``` private void sleep() ``` qui va endormir la pompe à event 

## RendezVous 
RendezVous est la classe qui permet à deux task de pouvoir se lié. Pour cela il aura trois fonctions : 
- un bind 
- un unbind
- un connect 

en suivant un peu la même logique que pour broker. Le RendezVous sera identifié par un numéro de port.