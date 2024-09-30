package task1;

import task1Interface.*;

public class Main {
	public static void main(String[] args) {

		// ChannelTest();
		MessageQueueTest();

	}
	
	// TEST : MessageQueue

	public static void MessageQueueTest() {
		Broker brokerServer = new Broker("server");
		Broker brokerClient = new Broker("client");

		BrokerManagement.getSelf().addBroker(brokerServer);
		BrokerManagement.getSelf().addBroker(brokerClient);
		
		QueueBroker queueServer = new QueueBroker(brokerServer);
		QueueBroker queueClient = new QueueBroker(brokerClient);

		Task server = new Task(queueServer, () -> {
			try {

				MessageQueue messageServer = queueServer.accept(88);

			} catch (Exception e) {
				System.out.println("Erreur dans le serveur: " + e.getMessage());
			}

		});
		
		Task client = new Task(queueClient, () -> {
			try {
				MessageQueue messageClient = queueClient.connect("server", 88);

				assert messageClient.closed() == false : "Le client est censé être connecter";

				byte[] sendData = new byte[256];
				for (int i = 0; i < sendData.length; i++) {
					sendData[i] = (byte) (i + 1);
				}

				messageClient.send(sendData, 0, sendData.length);

				byte[] receiveData = messageClient.receive();

				assert receiveData.length == sendData.length : "Le nombre d'octects lus et d'octects écrits ne sont pas les mêmes";

				messageClient.close();
				assert messageClient.closed() == true : "Le client est censé être déconnecter";

			} catch (Exception e) {
				System.out.println("Erreur dans le client: " + e.getMessage());
			}

		});

		try {
			server.start();
			client.start();

			server.join();
			client.join();

			assert server.getBroker() == brokerServer : "Il y'a un problème dans la méthode getBroker";
			assert client.getBroker() == brokerClient : "Il y'a un problème dans la méthode getBroker";
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	// TEST : Channel 
	public static void ChannelTest() {
		Broker brokerServer = new Broker("server");
		Broker brokerClient = new Broker("client");

		BrokerManagement.getSelf().addBroker(brokerServer);
		BrokerManagement.getSelf().addBroker(brokerClient);

		Task server = new Task(brokerServer, () -> {
			try {

				Channel channelServer = brokerServer.accept(88);

			} catch (Exception e) {
				System.out.println("Erreur dans le serveur: " + e.getMessage());
			}

		});

		Task client = new Task(brokerClient, () -> {
			try {
				Channel channelClient = brokerClient.connect("server", 88);

				assert channelClient.disconnected() == false : "Le client est censé être connecter";

				byte[] sendData = new byte[256];
				for (int i = 0; i < sendData.length; i++) {
					sendData[i] = (byte) (i + 1);
				}

				int writeData = channelClient.write(sendData, 0, sendData.length);

				assert writeData == sendData.length : "Le nombre d'octects envoyés et d'octects écrits ne sont pas les mêmes";
				
				byte[] receiveData = new byte[256];

				int readData = channelClient.read(receiveData, 0, receiveData.length);

				assert readData == writeData : "Le nombre d'octects lus et d'octects écrits ne sont pas les mêmes";

				channelClient.disconnect();
				assert channelClient.disconnected() == true : "Le client est censé être déconnecter";

			} catch (Exception e) {
				System.out.println("Erreur dans le client: " + e.getMessage());
			}

		});

		try {
			server.start();
			client.start();

			server.join();
			client.join();

			assert server.getBroker() == brokerServer : "Il y'a un problème dans la méthode getBroker";
			assert client.getBroker() == brokerClient : "Il y'a un problème dans la méthode getBroker";
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
