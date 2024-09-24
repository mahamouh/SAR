package task1;

import java.util.HashMap;

public class BrokerManagement {
	private HashMap<String, Broker> brokers;
	
	public BrokerManagement() {
		this.brokers = new HashMap<String, Broker>();
	}
	
	public void addBroker(Broker broker) {
		this.brokers.put(broker.getName(), broker);
	}
	
	public void removeBroker(Broker broker) {
		this.brokers.remove(broker.getName());
	}
	
	public Broker getBroker(String name) {
		Broker broker = this.brokers.get(name);
		return broker;
	}

}
