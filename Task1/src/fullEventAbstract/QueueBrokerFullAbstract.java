package fullEventAbstract;

import fullEvent.MessageQueueFull;

public interface QueueBrokerFullAbstract {
	
	public interface IQueueBrokerAcceptListener {
		void accepted(MessageQueueFull queue);
	}

	public abstract boolean bind(int port, IQueueBrokerAcceptListener listener);

	public abstract boolean unbind(int port);

	public interface IQueueBrokerConnectListener {
		void connected(MessageQueueFull queue);

		void refused();
	}

	public abstract boolean connect(String name, int port, IQueueBrokerConnectListener listener);
}
