package task3Test;

import event.EventPump;
import event.Message;
import event.MessageQueueEvent;
import event.QueueBrokerEvent;
import event.QueueBrokerManager;
import event.TaskEvent;
import eventAbstract.QueueBrokerEventAbstract;
import task1.Broker;
import task1.BrokerManagement;

public class EchoServer {

	public final static int messageSize = 250;

	public static void main(String[] args) {

		Runnable serverRunnable = new Runnable() {
			public void run() {
				BrokerManagement brokerManagement = BrokerManagement.getSelf();
				Broker brokerServer = new Broker("server");
				brokerManagement.addBroker(brokerServer);

				QueueBrokerManager management = QueueBrokerManager.getSelf();
				QueueBrokerEvent serverQueueBroker = new QueueBrokerEvent("server");
				serverQueueBroker.setBroker(brokerServer);
				management.addBroker(serverQueueBroker);
		        

				MyQueueAcceptListener listener = new MyQueueAcceptListener();
				boolean bound = serverQueueBroker.bind(8080, listener);
				if (!bound) {
					System.out.println("Server failed to bind");
					return;
				}
			}
		};

		Runnable clientRunnable = new Runnable() {
			public void run() {
				BrokerManagement brokerManagement = BrokerManagement.getSelf();
				Broker brokerClient = new Broker("client");
				brokerManagement.addBroker(brokerClient);
				
				QueueBrokerManager management = QueueBrokerManager.getSelf();
				QueueBrokerEvent clientQueueBroker = new QueueBrokerEvent("client");
				clientQueueBroker.setBroker(brokerClient);
				management.addBroker(clientQueueBroker);
				
				MyQueueConnectListener listener = new MyQueueConnectListener();
				boolean connected = clientQueueBroker.connect("server", 8080, listener);
				if (!connected) {
					System.out.println("Client failed to connect");
					return;
				}
			}
		};

		new TaskEvent().post(serverRunnable);
		
		try {
            Thread.sleep(1000); // Wait for the server to starts
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
		
		new TaskEvent().post(clientRunnable);
		EventPump.getSelf().run();
	}
}

class MyEchoServerQueueListener implements MessageQueueEvent.IListener {

	private MessageQueueEvent queue;

	public MyEchoServerQueueListener(MessageQueueEvent queue) {
		this.queue = queue;
	}

	@Override
	public void received(byte[] bytes) {
		System.out.println("Server received message");
		try {
			boolean sent = queue.send(bytes);
			if (!sent) {
				System.out.println("Server failed to send response");
			}
		} catch (Exception e) {
			System.out.println("Server failed to send message: " + e.getMessage());
		}
	}

	@Override
	public void closed() {
		System.out.println("Server finished");
	}

	@Override
	public void sent(Message message) {
		System.out.println("Server sent response");
	}
}

class MyEchoClientQueueListener implements MessageQueueEvent.IListener {

	private MessageQueueEvent queue;

	public MyEchoClientQueueListener(MessageQueueEvent queue) {
		this.queue = queue;
	}

	@Override
	public void received(byte[] bytes) {
		System.out.println("Client received response");

		// Check if the response is correct
		int messageSize = EchoServer.messageSize;
		byte[] messageContent = new byte[messageSize];
		for (int i = 0; i < messageSize; i++) {
			messageContent[i] = (byte) (i + 1);
		}

		for (int i = 0; i < messageSize; i++) {
			if (bytes[i] != messageContent[i]) {
				System.out.println("Client received incorrect response");
				return;
			}
		}

		EventPump.getSelf().kill();
		System.out.println("Test passed");
	}

	@Override
	public void closed() {
		System.out.println("Client finished");
	}

	@Override
	public void sent(Message message) {
		System.out.println("Client sent message");
	} 

}

class MyQueueAcceptListener implements QueueBrokerEventAbstract.IAcceptListener {

	@Override
	public void accepted(MessageQueueEvent queue) {
		System.out.println("Server accepted connection");
		MyEchoServerQueueListener listener = new MyEchoServerQueueListener(queue);
		queue.setListener(listener);
	}

}

class MyQueueConnectListener implements QueueBrokerEventAbstract.IConnectListener {

	@Override
	public void connected(MessageQueueEvent queue) {
		System.out.println("Connection established for client");
		int messageSize = EchoServer.messageSize;
		byte[] messageContent = new byte[messageSize];
		for (int i = 0; i < messageSize; i++) {
			messageContent[i] = (byte) (i + 1);
		}

		MyEchoClientQueueListener listener = new MyEchoClientQueueListener(queue);
		queue.setListener(listener);


		boolean sent = queue.send(messageContent);
		if (!sent) {
			System.out.println("Client failed to send message");
		}
	}

	@Override
	public void refused() {
		System.out.println("Connection refused");
	}
}