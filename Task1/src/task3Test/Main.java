package task3Test;

import event.EventPump;
import event.Message;
import event.MessageQueueEvent;
import event.QueueBrokerEvent;
import eventAbstract.QueueBrokerEventAbstract;
import task1.Task;

public class Main {
	
	public final static int messageSize = 5000;
	
    public static void main(String[] args) {
    	
    	    	
		QueueBrokerEvent serverQueueBroker = new QueueBrokerEvent("server");	
        new Task(serverQueueBroker, () -> runServerMessage(messageSize));
        
        try {
            Thread.sleep(1000); // Wait for the server to starts
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        QueueBrokerEvent clientQueueBroker = new QueueBrokerEvent("client");
        new Task(clientQueueBroker, () -> runClientMessage(messageSize));
        
        EventPump.getSelf().run();
    }
    
    private static void runServerMessage(int messageSize) {
		QueueBrokerEvent queueBroker = Task.getTask().getQueueBrokerEvent();
		MyAcceptListener listener = new MyAcceptListener();
		boolean bound = queueBroker.bind(8080, listener);
		
		if (!bound) {
			System.out.println("Server failed to bind");
			return;
		}
	}
	
	private static void runClientMessage(int messageSize) {
		QueueBrokerEvent queueBroker = Task.getTask().getQueueBrokerEvent();
		MyConnectListener listener = new MyConnectListener();
		boolean connected = queueBroker.connect("server", 8080, listener);
		if (!connected) {
			System.out.println("Client failed to connect");
			return;
		}
	}

}



class MyEchoServerListener implements MessageQueueEvent.IListener {
	
	private MessageQueueEvent queue;
	
	public MyEchoServerListener(MessageQueueEvent queue) {
		this.queue = queue;
	}

	@Override
	public void received(byte[] bytes) {
		System.out.println("Server received message");
        try {
            queue.send(bytes);
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
		queue.close();
	}
}

class MyEchoClientListener implements MessageQueueEvent.IListener {
	
	private MessageQueueEvent queue;
	
	public MyEchoClientListener(MessageQueueEvent queue) {
		this.queue = queue;
	}

	@Override
	public void received(byte[] bytes) {
		System.out.println("Client received response");
		
		// Check if the response is correct
		int messageSize = Main.messageSize;
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
		
		System.out.println("Test passed");
	}

	@Override
	public void closed() {
		System.out.println("Client finished");
	}

	@Override
	public void sent(Message message) {
		System.out.println("Client sent message");
		queue.close();
	}
	
}	

class MyAcceptListener implements QueueBrokerEventAbstract.IAcceptListener {

	@Override
	public void accepted(MessageQueueEvent queue) {
		System.out.println("Server accepted connection");
		MyEchoServerListener listener = new MyEchoServerListener(queue);
		queue.setListener(listener);
		
	}
	
}

class MyConnectListener implements QueueBrokerEventAbstract.IConnectListener {

	@Override
	public void connected(MessageQueueEvent queue) {
		System.out.println("Connection established for client");
		int messageSize = Main.messageSize;
		byte[] messageContent = new byte[messageSize];
		for (int i = 0; i < messageSize; i++) {
			messageContent[i] = (byte) (i + 1);
		}
		
		MyEchoClientListener listener = new MyEchoClientListener(queue);
		queue.setListener(listener);

		queue.send(messageContent);
	}

	@Override
	public void refused() {
		System.out.println("Connection refused");
	}
	
}