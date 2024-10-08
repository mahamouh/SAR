package eventAbstract;


import event.MessageQueueEvent;

public abstract class QueueBrokerEventAbstract {
	protected String name;

	public QueueBrokerEventAbstract(String name) {
		this.name = name;
	}

	public interface IAcceptListener {
		void accepted(MessageQueueEvent queue);
	}

	public abstract boolean bind(int port, IAcceptListener listener);

	public abstract boolean unbind(int port);

	public interface IConnectListener {
		void connected(MessageQueueEvent queue);

		void refused();
	}

	public abstract boolean connect(String name, int port, IConnectListener listener);
}
