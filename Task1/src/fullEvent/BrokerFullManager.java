package fullEvent;

import java.util.HashMap;

public class BrokerFullManager {
private static BrokerFullManager self;
	
	static {
		self = new BrokerFullManager();
	}
	
	public static BrokerFullManager getSelf() {
		return self;
	}
	
	private HashMap<String, BrokerFull> brokerFull;
	
	private BrokerFullManager() {
		this.brokerFull = new HashMap<String, BrokerFull>();
	}
	
	public synchronized void addBroker(BrokerFull brokerFull) {
		String name = brokerFull.getName();
		BrokerFull b = this.brokerFull.get(name);
		if(b != null) {
			throw new IllegalStateException("Broker " + name + " already exists");
		}
		this.brokerFull.put(name, brokerFull);
	}
	
	public synchronized void removeBroker(BrokerFull brokerFull) {
		this.brokerFull.remove(brokerFull.getName());
	}
	
	public synchronized BrokerFull getBroker(String name) {
		return this.brokerFull.get(name);
	}
}
