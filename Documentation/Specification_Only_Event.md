
### Spécification Full Events (Task4)

# AVANT DE COMMENCER
Avant de commencer, nous partons du principe que vous avez pris conscience de la spécification de la tâche 3 (Mixed Events/Thread) qui contient les nouvelles classes QueueBrokerEvent, MessageQueueEvent, et EventPump. 

# Nouvelle Spécification
Pour la tâche précédente (Task3),nous étions dans un monde mixte Event-Thread qui recevait des événements (TaskEvents) via une pompe à événement (EventPump) mais aussi des threads (pour exécuter les tâches bloquantes telles que write, read, etc.).

Nous reprenons complètement la spécification de la plus basse couche (Broker/Channel/Rdv), qui était basée uniquement sur des threads lors des tâches précédentes, ce qui n'est désormais plus le cas (Full Events).

# Broker 
Comme vous le savez, la classe Broker (des tasks précédentes) contient les méthodes accept et connect afin de pouvoir créer un canal de communication entre un serveur et un client (ou plus généralement entre deux brokers). Sauf que pour cela, nous avions un des deux (soit le client, soit le serveur) qui venait initier un Rdv et attendre que l'autre arrive pour répondre au Rdv. Cela générait les deux Channel que le serveur et le client afin besoin afin de communiquer. 

Dans un environnement désormais entièrement événementiel, les brokers n'attendront plus passivement la réponse de l'autre partie pour établir le rendez-vous, car aucune méthode bloquante n'est autorisée. Ainsi, au lieu de bloquer en attendant la connexion, les brokers utiliseront des listeners pour être notifiés lorsqu'un bind, un unbind ou un connect a été effectué, permettant ainsi de poursuivre le processus de connexion sans interruption.

Pour cela, nous avons besoin de deux listeners :

**AcceptBrokerListener** : reçoit la notification lorsque le bind a été effectué avec succès.
**ConnectBrokerListener** : reçoit la notification lorsque la tentative de connexion a été réussie ou a échoué.

Ces listeners permettent de poursuivre le processus de connexion de manière non bloquante, en étant notifiés des événements clés, sans interrompre le flux d'exécution.

Comment utiliser ces classes ? 

## AcceptBrokerListener

L'AcceptBrokerListener est un listener utilisé pour recevoir une notification lorsqu'un autre broker se connecte avec succès, ou si une connexion échoue. Le listener est notifié et peut alors réagir à l'événement de manière appropriée.


``` accepted(Channel queue) ``` : Cette méthode est appelée lorsque la connexion a été acceptée avec succès par le broker. Elle reçoit un objet Channel représentant le canal de communication établi entre les deux brokers.


La classe Broker a une méthode bind qui est : ``` boolean bind (int port, AcceptBrokerListener listener) ``` : 
- port : le port sur lequel le Broker souhaite faire un accept
- listener : le listener est notifié par le Broker que la méthode accept() a été réalisée avec succès, via ``` accepted(Channel queue) ```.

## ConnectBrokerListener

Le ConnectBrokerListener est un listener utilisé pour recevoir une notification lorsqu'un broker a réussi à se connecter à un autre broker, ou si la tentative de connexion a échoué.

``` connected(Channel queue) ``` :  Cette méthode est appelée lorsque la connexion a été réussie. Elle reçoit un objet Channel représentant le canal de communication entre les brokers.

``` refused() ``` :  Cette méthode est appelée lorsque la tentative de connexion échoue, par exemple, si le port est fermé/déjà utilisé ou si le broker distant refuse la connexion.


Broker a une méthode connect : ``` boolean connect(String name, int port, ConnectBrokerListener listener) ``` : 
- name : le nom du Broker avec qui on souhaite se connecter
- port : le port sur lequel le Broker veut se connecter
- listener : le listener est notifié par le Broker dès qu'un autre Broker (ayant fait un accept) peut se connecter à lui et réaliser une connexion. Si la connexion a bien été établie, la méthode va notifier le listener via ``` connected(Channel queue) ```, mais dans le cas contraire le listener sera notifié via ``` refused() ``` .

# Channel 

Puisque les méthodes write() et read() ne sont plus bloquantes, si la tâche d'écriture ou de lecture n'a pas pu être terminée en une seule fois, on notifie seulement l'utilisateur combien d'octets ont pu être lus ou écrits du message.
On considère que c'est à l'utilisateur de re-poster une nouvelle tâche s'il souhaite finir la lecture ou l'écriture de son message.

## Write 
``` int write(byte[] bytes, int offset, int length) ``` 
- bytes : le tableau de bytes où il y'a les bytes à lire
- offset : l'indice de départ
- len : la longueur à lire
- return : le nombre de bytes écrites
Donc il faut que le tableu accepte la longueur offset et offset+len entre le départ et la fin.
Des qu'un write a été fini, nous allons utiliser utiliser la méthode ``` void wrote(Message msg) ``` de ce ListenerChannel et appelle la méthode ``` void availaible(Message msg) ```
du channel remote.

Cette méthode retourne le nombre de bytes effectivement écrits.

Le tableau doit donc être dimensionné pour accepter les données à partir de l'indice offset jusqu'à offset + length. 
Une fois l'écriture d'un message terminée, la méthode wrote() du ListenerChannel est appelée pour notifier que l'écriture de tant d'octets a été effectuée.

## Read 
``` int read(byte[] bytes, int offset, int length)``` 
- bytes : le tableau de bytes où il y'a les bytes à lire
- offset : l'indice de depart
- length : la longueur à lire
- 
Cette méthode retourne le nombre de bytes effectivement lus.

Le tableau doit donc être dimensionné pour accepter les données à partir de l'indice offset jusqu'à offset + length. 
Une fois l'écriture d'un message terminée, la méthode read() du ListenerChannel est appelée pour notifier que la lecture de tant d'octets a été effectuée.

## ListenerChannel

Pour gérer les notifications et permettre à une tâche d'agir sur l'état de la lecture/écriture, nous introduisons une nouvelle interface : ListenerChannel.

ListenerChannel : Cette interface est utilisée pour notifier un gestionnaire d'événements (ou un listener) de certains événements concernant l'état du canal, tels que la possibilité de lire ou d'écrire des messages, ou encore la fermeture de la connexion.

``` wrote(byte[] bytes) ``` : est appelée pour notifier qu'un certain nombre d'octets de bytes ont été écrits avec succès.
``` read(Message msg) ```: est appelée pour notifier qu'un certain nombre d'octets du message ont été lus avec succès.
``` disconnect() ```: est appelée pour notifier que le Channel a été déconnecté.
``` available() ``` : est appelée pour notifier qu'il reste de la place dans le Channel.


Ainsi, grâce à ListenerChannel, la tâche n'a pas à attendre que l'écriture ou la lecture soit terminée avant de continuer son exécution. Cela permet une gestion asynchrone et non bloquante de l'envoi et de la réception des messages.

# RendezVous 

Nous avons décidé de reprendre le concept de semi-RDV : 

la connexion entre un client et serveur (deux brokers) se fera désormais uniquement si l'accept a été fait avant le connect. L'accept attend le connect, mais le connect n'attend pas le accept (=connexion refusée).

Cette classe possède deux méthodes : 
``` Channel accept(Broker brokerAccept) ```: renvoie le canal de communication pour le broker accept, que la connexion soit faite ou non.
``` Channel connect(Broker brokerConnect) ``` : renvoie le canal de communication établi pour le connect, si la connexion a été établie avec succès.

Chaque rendez-vous est identifié par son numéro de port.

# Task 
La classe Task permet de gérer l'exécution asynchrone de tâches. Elle fournit des méthodes pour ajouter des tâches à exécuter, arrêter ces tâches et vérifier leur état.

Méthodes : 
- ``` void post(Runnable r) ``` : permet d'ajouter une tâche (représentée par un Runnable) pour exécution asynchrone.
- ``` static Task task()``` : Renvoie l'instance de la tâche courante.
- ``` void kill() ``` : Arrête la tâche en cours, empêchant son exécution future.
- ``` boolean killed() ``` : Indique si la tâche a été arrêtée.


# MessageQueue

## Méthodes de MessageQueue

- ``` boolean send(Message msg) ``` : pour envoyer le message et retourne true si l'envoi est accepté (s'il y a assez de places dans le canal de communication).

Dès que l'envoi d'un message (send) a été fini,  nous notifions le MessageQueueListener via la méthode ``` void sent(Message msg) ```.

* ``` void receive() ``` : pour recevoir un message.

Dès que la réception d'un message (receive) a été fini,  nous notifions le MessageQueueListener via la méthode ``` void received(byte[] msg) ```.

* ``` void close() ``` : 
* ``` boolean closed() ``` : indique si le canal de communication du MessageQueue est fermé ou non.

## MessageQueueListener

MessageQueueListener est une interface qui sert à notifier un gestionnaire d'événements des événements relatifs aux messages dans une file de messages (MessageQueue). Elle permet de réagir aux différentes actions sur les messages, comme leur réception, envoi, ou disponibilité.

``` received(byte[] msg) ```: est appelée lorsque de nouveaux messages ont été reçus, et que la tâche ou le gestionnaire doit traiter ou consommer ces messages. Elle fournit le contenu du message sous forme de tableau d'octets.

``` sent(Message msg) ```: est appelée lorsque le message a été envoyé avec succès. Cela permet de notifier qu'une tâche a terminé l'envoi d'un message.

``` closed() ``` : est appelée lorsque la file de messages est fermée. Cela permet de notifier qu'il n'est plus possible d'envoyer ou de recevoir des messages à partir de cette file.

``` available() ``` : est appelée lorsqu'il y a assez de places dans le canal de communication (et qu'il est possible d'écrire ou lire).


# QueueBroker 
QueueBroker est une classe abstraite utilisée pour gérer les connexions réseau et la communication via des files de messages (MessageQueue). Elle permet à un client de se connecter à un serveur, ou à un serveur d'accepter des connexions entrantes.

Dans ce contexte, QueueBroker utilise deux listeners : 

**AcceptListener** : reçoit la notification lorsque le bind a été effectué avec succès, permettant ainsi à la tâche de poursuivre le processus sans blocage.

**ConnectListener** : reçoit la notification lorsque la tentative de connexion a été réussie ou a échoué, permettant ainsi à la tâche de réagir en conséquence, sans bloquer l'exécution.

Ces listeners sont utilisés pour recevoir des notifications asynchrones concernant l'état de la connexion, permettant de continuer la gestion de la communication sans nécessiter d'attente bloquante.

Comment utiliser ces interfaces ? 

## AcceptListener

QueueBroker a une méthode bind qui est : ``` boolean bind (int port, AcceptListener listener) ``` : 
- port : le port où le QueueBroker souhaite faire un accept
- listener : la méthode va notifier le listener qu'un autre QueueBroker peut se connecter à lui et réaliser une connexion, via sa méthode ``` void accepted(MessageQueue messageQueue) ```.


## ConnectListener
QueueBroker a une méthode connect : ``` boolean connect(String name, int port, ConnectionListener listener) ``` : 
- name : le nom du Broker avec on veut se connecter
- port : le port où le QueueBroker souhaite faire un connect
- listener : la méthode va notifier le listener qu'un autre QueueBroker (qui a fait un bind) est libre et peut se connecter à lui , via sa méthode ``` void connected(MessageQueue messageQueue) ```.


# EventPump 
EventPump est une pompe à événements qui aura comme champ ``` List<Runnable> queue ```. Chaque Task va poster son Runnable grâce à la méthode ``` void post(Runnable r) ```.

Ici, EventPump étend la classe Thread, signifiant qu'il possède une méthode run(). Dans notre ças, on a un ``` public synchronized void run() ``` où on fait exécute tous les objets Runnables se trouvant dans notre pompe à événements.

Nous avons aussi : 
- Une méthode  ``` public synchronized void post(Runnable r) ``` afin d'ajouter des objets Runnable à notre pompe à événements.
- Une méthode ``` private void sleep() ``` qui va endormir la pompe à événements.

