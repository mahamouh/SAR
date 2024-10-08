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
	public synchronized MessageQueue accept(int port) {
		Channel channelAccept = broker.accept(port);
		if(channelAccept != null) {
			return new MessageQueue(channelAccept);
		} else {
			throw new IllegalStateException("IL y'a eu un problème lors de l'accept");
		}
		
	}

	@Override
	public synchronized MessageQueue connect(String name, int port) {
		Channel channelConnect = broker.connect(name, port);
		if(channelConnect != null) {
			return new MessageQueue(channelConnect);
		} else {
			throw new IllegalStateException("La connexion a été refusé");
		}
	}

	@Override
	public Broker getBroker() {
		return this.broker;
	}

}
