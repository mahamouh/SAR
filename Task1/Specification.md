## specification du client/server Task1

## AVANT DE COMMENCER : On part sur du TCP
- Un broker, ici, est là pour créer des canaux de communication entre le client et le serveur. Il est differencié par un nom et un port unique par entité (deux clients ne peuvent pas s'appeler toto 80 par exemple). 
- Le channel est là afin de faire un cannel de communique entre le client et le serveur. Ce channel est identifié par un port (par exemple client1 parle avec le serveur dans le cannal1 identifié par le port 80 et il y'a qye ce client avec le serveur qui en ont l'accès). Les octets ne perdent pas dans l'envoie et l'écriture. On a UN cannel entre le client et le serveur (full duplex).
- Le Task peut être un client ou un serveur. Il peut avoir N task par broker.
- Multi thread  sur le broker (Déjà expliqué pourquoi en haut) et mono thread sur le channel (Pourquoi mono thread ? Car en TCP, les octects ne se perdent pas et viennet dans l'ordre mais pas instantanement donc si deux threads écrivent en même temps, le buffer n'aura pas un message clair. Par exemple si Client1 envoie 5 octets et Client2 envoie 2 octets. Le channel écrit d'abord ceux du client1 mais comme c'est en TCP, on aura par exemple 3 octects d'abord écrit puis comme le task a finit sa requête le client 2 envoie ses 2 octets et ses octects sont bien écrits dans le buffer. Donc on aura 3 octects du Client1 puis 2 octects du Client2 puis ensuite 2 octects du Client1.)

## Package : 
- task1Interface : regroupe les interface de la Task1;
- task1 : regroupe les classes et leurs implementation

## Use case du client server 

- Pouvoir se connecter au serveur 
- Envoyer une suite d'octetcs au serveur 
- Pouvoir lire la suite d'octects qu'on a envoyé 
- Pouvoir se déconnecter


## Classe Broker : PCréer des canaux de communication entre le client et le serveur pour envoyer/recevoir des messages 
# Broker : créer un nouveau broker 
# Channel accept : accepter la connexion du client, créer un channel avec le port en paramètre et le renvoyer
# Channel connect : connecter le nouveau client dans le channel

```java
abstract class Broker {
    Broker(String name);
    Channel accept(int port);
    Channel connect(String name, int port);
}
```

## Classe Channel : Cannal de communication entre le client et le serveur pour envoyer/recevoir des messages 
# read : lire dans le buffer la suite d'octects mise en paramètre
# write : écrire dans le buffer la suite d'octects mise en paramètre
# void disconnect : déconnecter le client 
# boolean disconnected : voir si le client est deconnecté ou non

```java
abstract class Channel {
    int read(byte[] bytes, int offset, int length);
    int write(byte[] bytes, int offset, int length);
    void disconnect();
    boolean disconnected();
}
```

## Classe Task : Pour créer un thread pour être un client ou un serveur
# Task : on crée un nouveau task (thread) qui est soit un client, soit un serveur.
# getBroker : renvoyer le broker du task

```java
abstract class Task extends Thread{
    Task(Broker b, Runnable r);
    static Broker getBroker();
}
```