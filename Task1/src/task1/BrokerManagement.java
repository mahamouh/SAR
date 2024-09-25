package task1;

import java.util.HashMap;

public class BrokerManagement {
	private static BrokerManagement self;
	
	static {
		self = new BrokerManagement();
	}
	
	static BrokerManagement getSelf() {
		return self;
	}
	
	private HashMap<String, Broker> brokers;
	
	private BrokerManagement() {
		this.brokers = new HashMap<String, Broker>();
	}
	
	public synchronized void addBroker(Broker broker) {
		String name = broker.getName();
		Broker b = this.brokers.get(name);
		if(b != null) {
			throw new IllegalStateException("Broker " + name + " already exists");
		}
		this.brokers.put(name, broker);
	}
	
	public synchronized void removeBroker(Broker broker) {
		this.brokers.remove(broker.getName());
	}
	
	public synchronized Broker getBroker(String name) {
		return this.brokers.get(name);
	}

}
