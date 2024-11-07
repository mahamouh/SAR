package task4Test;

import event.EventPump;
import event.Message;
import event.TaskEvent;
import fullEvent.MessageQueueFull;
import fullEvent.QueueBrokerFull;
import fullEventAbstract.MessageQueueFullAbstract;
import fullEventAbstract.QueueBrokerFullAbstract;

public class TestMessageFull {
	public final static int messageSize = 250;

	public static void main(String[] args) {

		Runnable serverRunnable = new Runnable() {
			public void run() {
				QueueBrokerFull queueBroker = new QueueBrokerFull("server");

				MyQueueBrokerAcceptListener listener = new MyQueueBrokerAcceptListener();
				boolean bound = queueBroker.bind(8080, listener);

				if (!bound) {
					System.out.println("Server failed to bind");
					return;
				}
			}
		};

		Runnable clientRunnable = new Runnable() {
			public void run() {
				QueueBrokerFull queueBroker = new QueueBrokerFull("client");

				MyQueueBrokerConnectListener listener = new MyQueueBrokerConnectListener();
				boolean connected = queueBroker.connect("server", 8080, listener);
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

class MyEchoServerQueueListener implements MessageQueueFullAbstract.IMessageQueueListener {

	private MessageQueueFullAbstract queue;

	public MyEchoServerQueueListener(MessageQueueFullAbstract queue) {
		this.queue = queue;
	}

	@Override
	public void received(byte[] bytes) {
		System.out.println("Server received message");
		try {
			boolean sent = queue.send(new Message(bytes, 0, bytes.length));
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

	@Override
	public void availaible() {
		queue.receive();

	}
}

class MyEchoClientQueueListener implements MessageQueueFullAbstract.IMessageQueueListener {

	private MessageQueueFullAbstract queue;

	public MyEchoClientQueueListener(MessageQueueFullAbstract queue) {
		this.queue = queue;
	}

	@Override
	public void received(byte[] bytes) {
		System.out.println("Client received response");

		// Check if the response is correct
		int messageSize = TestMessageFull.messageSize;
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

		queue.close();

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

	@Override
	public void availaible() {
		queue.receive();
	}

}

class MyQueueBrokerAcceptListener implements QueueBrokerFullAbstract.IQueueBrokerAcceptListener {

	@Override
	public void accepted(MessageQueueFull queue) {
		System.out.println("Server accepted connection");
		MyEchoServerQueueListener listener = new MyEchoServerQueueListener(queue);
		queue.setListener(listener);
	}

}

class MyQueueBrokerConnectListener implements QueueBrokerFullAbstract.IQueueBrokerConnectListener {

	@Override
	public void connected(MessageQueueFull queue) {
		System.out.println("Connection established for client");
		int messageSize = TestMessageFull.messageSize;
		byte[] messageContent = new byte[messageSize];
		for (int i = 0; i < messageSize; i++) {
			messageContent[i] = (byte) (i + 1);
		}

		MyEchoClientQueueListener listener = new MyEchoClientQueueListener(queue);
		queue.setListener(listener);

		Message message = new Message(messageContent, 0, messageSize);
		boolean sent = queue.send(message);
		if (!sent) {
			System.out.println("Client failed to send message");
		}
	}

	@Override
	public void refused() {
		System.out.println("Connection refused");
	}


}
