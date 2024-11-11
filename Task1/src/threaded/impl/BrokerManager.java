package threaded.impl;

import java.util.HashMap;

public final class BrokerManager {
    
    private static final BrokerManager INSTANCE = new BrokerManager();
    
    private HashMap<String, Broker> brokerMap;
    
    private BrokerManager() {
        brokerMap = new HashMap<String, Broker>();
    }
    
    public static BrokerManager getInstance() {
        return INSTANCE;
    }
    
    public Channel connect(Broker connectingBroker, String name, int port) {
        Broker targetBroker = (Broker) brokerMap.get(name);
        
        if (targetBroker == null) {
            return null;
        }
        
        return targetBroker.connect(connectingBroker, port);
    }
    
    public synchronized Broker getBroker(String name) {
        Broker targetBroker = (Broker) brokerMap.get(name);
        
        return targetBroker;
    }
    
    public synchronized void addBroker(Broker broker) {
        
        Broker brokerImpl = (Broker) broker;
        String brokerName = brokerImpl.getBrokerName();
        
        if (brokerMap.containsKey(brokerName)) {
            throw new IllegalStateException("Two Brokers have the same name");
        }
        
        this.brokerMap.put(brokerName, broker);
    }
}
