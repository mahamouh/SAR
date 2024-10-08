package event;

import java.util.HashMap;


public class QueueBrokerManager {
private static QueueBrokerManager self;
	
	static {
		self = new QueueBrokerManager();
	}
	
	public static QueueBrokerManager getSelf() {
		return self;
	}
	
	private HashMap<String, QueueBrokerEvent> queueBrokerEvent;
	
	private QueueBrokerManager() {
		this.queueBrokerEvent = new HashMap<String, QueueBrokerEvent>();
	}
	
	public synchronized void addBroker(QueueBrokerEvent queueBrokerEvent) {
		String name = queueBrokerEvent.getName();
		QueueBrokerEvent b = this.queueBrokerEvent.get(name);
		if(b != null) {
			throw new IllegalStateException("Broker " + name + " already exists");
		}
		this.queueBrokerEvent.put(name, queueBrokerEvent);
	}
	
	public synchronized void removeBroker(QueueBrokerEvent queueBrokerEvent) {
		this.queueBrokerEvent.remove(queueBrokerEvent.getName());
	}
	
	public synchronized QueueBrokerEvent getBroker(String name) {
		return this.queueBrokerEvent.get(name);
	}
}
