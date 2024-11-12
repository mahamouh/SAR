package fullEvent.test;

import event.EventPump;
import event.Message;
import event.TaskEvent;
import fullEvent.abst.MessageQueueFullAbstract;
import fullEvent.abst.QueueBrokerFullAbstract;
import fullEvent.impl.BrokerFullManager;
import fullEvent.impl.MessageQueueFull;
import fullEvent.impl.QueueBrokerFull;
import fullEvent.impl.QueueBrokerFullManager;

public class TestMessageFull2 {
	public final static int messageSize = 250;

	public static void main(String[] args) {

		Runnable serverRunnable = new Runnable() {
			public void run() {
				QueueBrokerFull queueBroker = new QueueBrokerFull("server");

				MyQueueBrokerAcceptListener2 listener = new MyQueueBrokerAcceptListener2();
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

				MyQueueBrokerConnectListener2 listener = new MyQueueBrokerConnectListener2();
				boolean connected = queueBroker.connect("server", 8080, listener);
				if (!connected) {
					System.out.println("Client failed to connect");
					return;
				}
			}
		};

		Runnable clientRunnable2 = new Runnable() {
			public void run() {
				QueueBrokerFull queueBroker = new QueueBrokerFull("client2");

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
		new TaskEvent().post(clientRunnable2);
		EventPump.getSelf().run();

	}

	public static void ajoutClient3() {
		BrokerFullManager brokerManagement = BrokerFullManager.getSelf();
		if (brokerManagement.getBroker("client3") == null) {
			Runnable clientRunnable3 = new Runnable() {
				public void run() {

					QueueBrokerFull queueBroker = new QueueBrokerFull("client3");

					MyQueueBrokerConnectListener listener = new MyQueueBrokerConnectListener();
					boolean connected = queueBroker.connect("server", 8080, listener);
					if (!connected) {
						System.out.println("Client failed to connect");
						return;
					}
				}
			};

			new TaskEvent().post(clientRunnable3);
		}
	}
}

class MyEchoServerQueueListener2 implements MessageQueueFullAbstract.IMessageQueueListener {

	private MessageQueueFullAbstract queue;

	public MyEchoServerQueueListener2(MessageQueueFullAbstract queue) {
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

class MyEchoClientQueueListener2 implements MessageQueueFullAbstract.IMessageQueueListener {

	private MessageQueueFullAbstract queue;

	public MyEchoClientQueueListener2(MessageQueueFullAbstract queue) {
		this.queue = queue;
	}

	@Override
	public void received(byte[] bytes) {
		System.out.println("Client received response");

		// Check if the response is correct
		int messageSize = TestMessageFull2.messageSize;
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
		
		QueueBrokerFullManager.getSelf().getBroker("server").clearRDV((MessageQueueFull) queue);
		TestMessageFull2.ajoutClient3();

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

class MyQueueBrokerAcceptListener2 implements QueueBrokerFullAbstract.IQueueBrokerAcceptListener {

	@Override
	public void accepted(MessageQueueFull queue) {
		System.out.println("Server accepted connection");
		MyEchoServerQueueListener2 listener = new MyEchoServerQueueListener2(queue);
		queue.setListener(listener);
	}

}

class MyQueueBrokerConnectListener2 implements QueueBrokerFullAbstract.IQueueBrokerConnectListener {

	@Override
	public void connected(MessageQueueFull queue) {
		System.out.println("Connection established for client");
		int messageSize = TestMessageFull2.messageSize;
		byte[] messageContent = new byte[messageSize];
		for (int i = 0; i < messageSize; i++) {
			messageContent[i] = (byte) (i + 1);
		}

		MyEchoClientQueueListener2 listener = new MyEchoClientQueueListener2(queue);
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