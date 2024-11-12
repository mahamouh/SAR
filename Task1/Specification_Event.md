### Specification Event Queue

# AVANT DE COMMENCER
Avant de commencer, nous partons du principe que vous avez pris conscience de la specification de channel, des messageQueue en bloquant. Que vous avez ce qu'un broker, un channel et le fonctionnement de messageQueue.


# Nouvelle Specification
Ici, ce qui va changer, c'est la manière d'utiliser et d'implementer les messageQueue. Précedament, nous avons écrit et utilisé les messageQueue comme étant des méthodes bloquants. C'est à dire, nous avions plusieurs thread qui avez chacun leurs propres actions à réaliser. Comme messageQueue était bloquant, certains thread étaient bloqué et attendaient qu'un autre thread vienne les reveiller.

Ici, nous n'allons plus utiliser des méthodes bloquantes mais plutot de l'évènementiel. On aura un thread principal qui va exécuter le programme, lors qu'une méthode qui devrait être soit disante "bloquante", nous allons placer des listener qui va notifier le thread quand il pourra poursuivre son précédent éxcétution et en attendant la notification du listener, le thread pourra effectuer d'autres actions sans être bloquer.


## QueueBroker 
QueueBroker est une classe abstraite utilisée pour gérer les connexions réseau et la communication via des files de messages (MessageQueue). Elle permet à un client de se connecter à un serveur ou à un serveur d'accepter des connexions entrantes.

Pour QueueBroker, nous aurons deux interfaces listener : un AcceptListener et un ConnectListener

- AcceptListener : est là afin de notifier le thread quand un autre thread a repondu à son accept
- ConnectListener : est là afin de notifier le thread quand un autre thread a repondu à son connect

On crée des méthodes AcceptListener et ConnectListener puisque ces deux méthodes sont bloquantes. Il faut notifier quand est-ce que le accept et connect ont été acceptées et arrêtées d'être bloquées.

Comment utiliser ces interfaces ? 

# AcceptListener
QueueBroker a une méthode bind qui est : ``` boolean bind (int port, AcceptListener listener) ``` : 
- port : le port où le thread est prêt à faire un accept
- listener : le listener qui va le notifier dès qu'un thread peut se connecter à lui et réaliser une conenxion.

Si tout se passe bien, le listener va appeler sa méthode ``` void accepted(MessageQueue messageQueue) ``` une connexion sera faite. On peut aussi avoir une queueBorker qui n'est plus prêt à faire des accept sur ce port et appelle la fonction unblind : ``` boolean unbind (int port) ``` pour dire qu'une connexion n'est possible.

# ConnectListener
QueueBroker a une méthode connect : ``` boolean connect(String name, int port, ConnectionListener listener) ``` : 
- name : le nom du Broker avec qui le thread veut se connecter
- port : le port où le thread veut se connecter
- listener : le listener qui va le notifier dès qu'un autre thread (qui a fait bind) est libre et peut se connecter.

Si tout se passe bien, le listener va appeler sa méthode ``` void connected(MessageQueue messageQueue) ``` et une connexion sera faite sinon la fonction refused : ``` void refused() ``` sera appelé pour dire qu'une connexion n'est pas possible/n'aura pas lieu sur ce port.


## MessageQueue
Pour MessageQueue, nous aurons un interface listener : Listener

- Listener : est là afin de notifier le thread quand il pourra lire les messages qu'un autre thread lui a envoyé.

Comment utiliser cette interfaces ? 

# Send 
MessageQueue a deux méthodes send 

- ``` boolean send(Message msg) ``` : pour envoyer le msg et return true si le thread accept l'envoie
- ``` boolean send(Message msg) ``` : pour envoyer le msg et écrire une longueue len et return true si le thread accept l'envoie

Des qu'un send a été fini, à la fin nous allons créer un listener et l'envoyer à l'autre thread car la méthode ``` void setListener(listener l) ``` et grâce à ça nous pouvons utiliser la méthode ``` void received(byte[] msg) ``` de ce listener.


# Close et closed
Pour ce qui est de la déconnexion, un thread appelle sa méthode ``` void close() ```  et le listener notifie à l'autre thread grâce au méthode 
``` void closed()``` afin de lui dire que la connexion a été coupée.


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