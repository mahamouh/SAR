# Projet SAR

Ce projet contient:
- **task 1:** Channel et Broker avec Thread
- **task 2:** MessageQueue et QueueuBroker avec Thread
- **task 3:** Channel et Broker avec Thread et MessageQueue et QueueBroker avec Event
- **task 4:** Channel et Broker avec Event
- **task 5:** MessageQueue et QueueBroker avec Event

Tout le code et tests sont dans la branche **main**.
Les documentations (specification et designs) sont aussi dans la branche **main**.

## Task1
La task est dans le dossier threaded (abst, test, impl)
Le test correspondant est *EchoTest.java*<br>
Le scénario du test est un echoserver.

## Task2
La task2 se trouve dans la branche Task2
Le test correspondant est *EchoServer.java*<br>
Le scénario du test est un echoserver.

## Task3
La task est dans le dossier mixed (abst, test, impl)
Le test correspondant est *GeneralizedTest.java* et *Test1.java*<br>
Le scénario du premier test est un echoserver.

## Task4
La task4 et 5 se trouve dans le dossier fullEvent (abst, test, impl)
Le test correspondant est *TestChannel.java* <br>

Le scénario du test est:
1. Un client1 et un serveur établissent une connection
2. Un client2 essaie de se connecter au server
3. Fin de connection entre client1 et serveur
4. Un client3 et un serveur établissent une connection

## Task5
Les tests correspondant sont *TestMessageFull.java* et *TestMessageFull2*<br>
Le scénario du premier test est un simple echoserver.

Le scénario du deuxième test est:
1. Un client1 et un serveur établissent une connection
2. Un client2 essaie de se connecter au server
3. Fin de connection entre client1 et serveur
4. Un client3 et un serveur établissent une connection


## Participantes
Asmae El kanbi<br>
Hannane Mahamoudou<br>
Louane Lesur<br>
Rana Rochdi
