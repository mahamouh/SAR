package event;

import java.util.HashMap;
import java.util.Map;

import eventAbstract.QueueBrokerEventAbstract;
import task1.Broker;
import task1.BrokerManagement;
import task1.Channel;
import task1.QueueBroker;

public class QueueBrokerEvent extends QueueBrokerEventAbstract {
	private Map<Integer, Thread> accepts = new HashMap<>();
	private Broker broker;
	private Channel channelAccept;
	private Channel channelConnect;

	public QueueBrokerEvent(String name) {
		super(name);
	}

	public void setBroker(Broker broker) {
		this.broker = broker;
	}

	public Broker getBroker() {
		return this.broker;
	}

	public String getName() {
		return super.name;
	}

	public Channel getChannelAccept() {
		return this.channelAccept;
	}

	public Channel getChannelConnect() {
		return this.channelConnect;
	}

	public void setChannelAccept(Channel channel) {
		this.channelAccept = channel;
	}

	public boolean bind(int port, IAcceptListener listener) {
		if (accepts.containsKey(port)) {
			System.out.println("Port " + port + " est déjà relié");
			return false;
		}

		Thread acceptThread = new Thread() {
			@Override
			public void run() {
				channelAccept = broker.accept(port);
				MessageQueueEvent messageQueueAccept = new MessageQueueEvent(channelAccept);
				AcceptEvent acceptEvent = new AcceptEvent(listener, messageQueueAccept);
				acceptEvent.postTask();
			}
		};

		accepts.put(port, acceptThread);
		acceptThread.start();
		return true;
	}

	public boolean unbind(int port) {
		if (accepts.get(port) != null) {
			Thread acceptThread = accepts.remove(port);
			acceptThread.interrupt();
			System.out.println("Port " + port + " a bien été enlévé");
			return true;
		}
		System.out.println("Port " + port + " n'a pas été trouvé");
		return false;
	}

	public boolean connect(String name, int port, IConnectListener listener) {

		QueueBrokerEvent queueBrokerServer = QueueBrokerManager.getSelf().getBroker(name);
		if (queueBrokerServer == null) {
			return false;
		}

		Thread connectThread = new Thread() {
			@Override
			public void run() {
				try {
					channelConnect = broker.connect(name, port);
					MessageQueueEvent messageQueueConnect = new MessageQueueEvent(channelConnect);
					ConnectEvent connectEvent = new ConnectEvent(listener, messageQueueConnect);
					connectEvent.postTask();
				} catch (Exception e) {
					listener.refused();
				}
			}
		};

		connectThread.start();
		return true;
	}

}
