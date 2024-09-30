package task1;

import task1Interface.IQueueBroker;

public class QueueBroker implements IQueueBroker{
	private Broker broker;
	private String name;

	public QueueBroker(Broker broker) {
		this.broker = broker;
		this.name = broker.name;
	}
	
	@Override
	public String name() {
		return this.name;
	}

	@Override
	public MessageQueue accept(int port) {
		Channel channelAccept = broker.accept(port);
		return new MessageQueue(channelAccept);
	}

	@Override
	public MessageQueue connect(String name, int port) {
		Channel channelConnect = broker.connect(name, port);
		return new MessageQueue(channelConnect);
	}

	@Override
	public Broker getBroker() {
		return this.broker;
	}

}
