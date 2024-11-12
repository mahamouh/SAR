package fullEvent.impl;

import java.util.HashMap;

public class QueueBrokerFullManager {
	private static QueueBrokerFullManager self;

	static {
		self = new QueueBrokerFullManager();
	}

	public static QueueBrokerFullManager getSelf() {
		return self;
	}

	private HashMap<String, QueueBrokerFull> queueBrokerFull;

	private QueueBrokerFullManager() {
		this.queueBrokerFull = new HashMap<String, QueueBrokerFull>();
	}

	public synchronized void addBroker(QueueBrokerFull queueBrokerFull) {
		String name = queueBrokerFull.getName();
		QueueBrokerFull b = this.queueBrokerFull.get(name);
		if (b != null) {
			throw new IllegalStateException("Broker " + name + " already exists");
		}
		this.queueBrokerFull.put(name, queueBrokerFull);
	}

	public synchronized void removeBroker(QueueBrokerFull brokerFull) {
		this.queueBrokerFull.remove(brokerFull.getName());
	}

	public synchronized QueueBrokerFull getBroker(String name) {
		return this.queueBrokerFull.get(name);
	}
}
