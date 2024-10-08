package task1;

import java.util.HashMap;

public class BrokerManagement {
	private static BrokerManagement self;
	
	static {
		self = new BrokerManagement();
	}
	
	public static BrokerManagement getSelf() {
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
			System.out.println("Broker " + broker.name + " existe deja");
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
