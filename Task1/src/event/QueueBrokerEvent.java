package event;

import java.util.HashMap;
import java.util.Map;

import eventAbstract.QueueBrokerEventAbstract;
import task1.Broker;
import task1.BrokerManagement;
import task1.Channel;
import task1.QueueBroker;

public class QueueBrokerEvent extends QueueBrokerEventAbstract {
	private Map<Integer, IAcceptListener> accepts = new HashMap<>();
	private Broker broker;
	private Channel channelAccept;
	private Channel channelConnect;
	Boolean bind = false;
	Boolean connect = false;

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
				AcceptEvent acceptEvent = new AcceptEvent(port, listener, accepts);
				acceptEvent.postTask();
				System.out.println("Port " + port + " a bien été relié");
				bind = true;
			}
		};
		acceptThread.start();
		return bind;
	}

	public boolean unbind(int port) {
		if (accepts.get(port) != null) {
			accepts.remove(port);
			System.out.println("Port " + port + " a bien été enlévé");
			return true;
		}
		System.out.println("Port " + port + " n'a pas été trouvé");
		return false;
	}

	public boolean connect(String name, int port, IConnectListener listener) {
		Thread connectThread = new Thread() {

			@Override
			public void run() {
				QueueBrokerEvent queueBrokerServer = QueueBrokerManager.getSelf().getBroker(name);
				if (queueBrokerServer == null) {
					connect = false;
				}

				IAcceptListener acceptListener = queueBrokerServer.accepts.get(port);

				if (acceptListener != null) {
					ConnectEvent connectEvent = new ConnectEvent(QueueBrokerEvent.this, name, port, listener);
					connectEvent.postTask();
					connect = true;
				} else {
					listener.refused();
					connect = false;
				}
			}
		};
		connectThread.start();
		return connect;
	}

	public void _connect(String name, int port, IConnectListener listener) {
		QueueBrokerEvent queueBrokerServer = QueueBrokerManager.getSelf().getBroker(name);
		IAcceptListener acceptListener = queueBrokerServer.accepts.get(port);
		channelConnect = broker.connect(name, port);
		if (acceptListener != null && channelConnect != null) {

			channelAccept = queueBrokerServer.getChannelAccept();

			MessageQueueEvent messageQueueConnect = new MessageQueueEvent(channelConnect);
			MessageQueueEvent messageQueueAccept = new MessageQueueEvent(channelAccept);

			messageQueueConnect.setRmMessageQueueEvent(messageQueueAccept);
			messageQueueAccept.setRmMessageQueueEvent(messageQueueConnect);

			acceptListener.accepted(messageQueueAccept);
			listener.connected(messageQueueConnect);
		} else {
			listener.refused();
		}
	}
}
