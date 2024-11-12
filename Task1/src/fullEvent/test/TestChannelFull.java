package fullEvent.test;

import event.EventPump;
import event.Message;
import event.TaskEvent;
import exception.DisconnectedException;
import fullEvent.abst.BrokerFullAbstract;
import fullEvent.abst.ChannelFullAbstract;
import fullEvent.impl.BrokerFull;
import fullEvent.impl.BrokerFullManager;
import fullEvent.impl.ChannelFull;

public class TestChannelFull {
	public final static int messageSize = 250;

	public static void main(String[] args) {

		Runnable serverRunnable = new Runnable() {
			public void run() {
				BrokerFullManager brokerManagement = BrokerFullManager.getSelf();
				BrokerFull brokerServer = new BrokerFull("server");
				brokerManagement.addBroker(brokerServer);
		        
				MyQueueAcceptListener listener = new MyQueueAcceptListener();
				boolean bound = brokerServer.bind(8080, listener);
				if (!bound) {
					System.out.println("Server failed to bind");
					return;
				}
			}
		};

		Runnable clientRunnable = new Runnable() {
			public void run() {
				BrokerFullManager brokerManagement = BrokerFullManager.getSelf();
				BrokerFull brokerClient = new BrokerFull("client");
				brokerManagement.addBroker(brokerClient);
				
				MyQueueConnectListener listener = new MyQueueConnectListener();
				boolean connected = brokerClient.connect("server", 8080, listener);
				if (!connected) {
					System.out.println("Client failed to connect");
					return;
				}
			}
		};
		
		Runnable clientRunnable2 = new Runnable() {
			public void run() {
				BrokerFullManager brokerManagement = BrokerFullManager.getSelf();
				BrokerFull brokerClient = new BrokerFull("client2");
				brokerManagement.addBroker(brokerClient);
				
				MyQueueConnectListener listener = new MyQueueConnectListener();
				boolean connected = brokerClient.connect("server", 8080, listener);
				if (!connected) {
					System.out.println("Client2 failed to connect");
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
		if(brokerManagement.getBroker("client3") == null) {
			Runnable clientRunnable3 = new Runnable() {
				public void run() {
					
					BrokerFull brokerClient = new BrokerFull("client3");
					brokerManagement.addBroker(brokerClient);
					
					MyQueueConnectListener listener = new MyQueueConnectListener();
					boolean connected = brokerClient.connect("server", 8080, listener);
					if (!connected) {
						System.out.println("Client3 failed to connect");
						return;
					}
				}
			};
			
			
			new TaskEvent().post(clientRunnable3);
		}	
	}
}

class MyEchoServerChannelListener implements ChannelFullAbstract.IChannelListener {

	private ChannelFull channel;
	
	public MyEchoServerChannelListener(ChannelFull channel) {
		this.channel = channel;
	}

	@Override
	public void wrote(byte[] bytes) {
		System.out.println("Server wrote a message");
		channel.disconnect();
	}

	@Override
	public void disconnected() {
		
		System.out.println("Server finished");
		
	}

	@Override
	public void read(Message msg) {
		System.out.println("Server read a message");
		try {
			channel.write(msg.getByte(), msg.getOffset(), msg.getLength());
		} catch (DisconnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void availaible() {
		byte [] bytes = new byte[TestChannelFull.messageSize];
		try {
			channel.read(bytes, 0, bytes.length);
		} catch (DisconnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}

class MyEchoClientChannelListener implements ChannelFullAbstract.IChannelListener {

	private ChannelFull channel;
	
	public MyEchoClientChannelListener(ChannelFull channel) {
		this.channel = channel;
	}

	@Override
	public void wrote(byte[] bytes) {
		System.out.println("Client wrote a message");
		
	}

	@Override
	public void disconnected() {
		System.out.println("Client finished");
	}

	@Override
	public void read(Message msg) {
		System.out.println("Client read a response");

		// Check if the response is correct
		int messageSize = TestChannelFull.messageSize;
		byte[] messageContent = new byte[messageSize];
		for (int i = 0; i < messageSize; i++) {
			messageContent[i] = (byte) (i + 1);
		}
		
		byte [] bytes = msg.getByte();
		for (int i = 0; i < messageSize; i++) {
			if (bytes[i] != messageContent[i]) {
				System.out.println("Client received incorrect response");
				return;
			}
		}

		channel.disconnect();
		
		BrokerFullManager.getSelf().getBroker("server").clearRdv(channel.getPort());
		TestChannelFull.ajoutClient3();
		
		System.out.println("Test passed");
	}

	@Override
	public void availaible() {
		byte [] bytes = new byte[TestChannelFull.messageSize];
		try {
			channel.read(bytes, 0, bytes.length);
		} catch (DisconnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 

}

class MyQueueAcceptListener implements BrokerFullAbstract.IBrokerAcceptListener {

	@Override
	public void accepted(ChannelFull queue) {
		System.out.println("Server accepted connection");
		MyEchoServerChannelListener listener = new MyEchoServerChannelListener(queue);
		queue.setListener(listener);
		
	}

}

class MyQueueConnectListener implements BrokerFullAbstract.IBrokerConnectListener {

	@Override
	public void refused() {
		System.out.println("Connection refused");
	}

	@Override
	public void connected(ChannelFull queue) {
		System.out.println("Connection established for client");
		int messageSize = TestChannelFull.messageSize;
		byte[] messageContent = new byte[messageSize];
		for (int i = 0; i < messageSize; i++) {
			messageContent[i] = (byte) (i + 1);
		}

		MyEchoClientChannelListener listener = new MyEchoClientChannelListener(queue);
		queue.setListener(listener);


		int sentingCpt = 0;
		try {
			sentingCpt = queue.write(messageContent, 0, messageContent.length);
		} catch (DisconnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (sentingCpt != messageContent.length) {
			System.out.println("Client failed to send message");
		}
		
	}

	
}
