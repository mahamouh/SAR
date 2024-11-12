package threaded.impl;

import java.util.LinkedList;
import java.util.List;

import threaded.abst.Task;

public class Broker extends threaded.abst.Broker {
    
    private String brokerName; 
    private BrokerManager brokerManager;
    private List<Rdv> rendezvousList;

    public Broker(String name) {
        super(name);
        this.brokerName = name;
        this.brokerManager = BrokerManager.getInstance();
        this.rendezvousList = new LinkedList<Rdv>();
        
        brokerManager.addBroker(this);
    }

    @Override
    public Channel accept(int port) {
        Rdv rdvInstance = null;
        synchronized (rendezvousList) {
            for (Rdv r : this.rendezvousList) {
                if (r.isAccept()) {
                    throw new IllegalStateException("Two Accepts on the same Broker");
                }
                if (r.isConnect() && r.getPort() == port) {
                    rdvInstance = r;
                    rdvInstance.setAcceptBroker(this);
                    break;
                }
            }
            
            if (rdvInstance == null) {
                rdvInstance = new Rdv(port);
                rdvInstance.setAcceptBroker(this);
                this.rendezvousList.add(rdvInstance);
            } else {
                rendezvousList.remove(rdvInstance);
            }
        }
        try {
            return rdvInstance.accept();
        } catch (Exception e) {
            rendezvousList.remove(rdvInstance);
            return null;
        }
    }

    @Override
    public Channel connect(String name, int port) {
        
        Broker remoteBrokerInstance = (Broker) brokerManager.getBroker(name);
        if (remoteBrokerInstance == null) {
            return null;
        }
        
        return remoteBrokerInstance.connect(this, port);
    }
    
    
    Channel connect(Broker brokerToConnect, int port) {
        Rdv rdvInstance = null;
        synchronized (rendezvousList) {
            for (Rdv r : this.rendezvousList) {
                if (r.isAccept() && r.getPort() == port) {
                    rdvInstance = r;
                    rdvInstance.setConnectBroker(brokerToConnect);
                    break;
                }
            }
            
            if (rdvInstance == null) {
                rdvInstance = new Rdv(port);
                rdvInstance.setConnectBroker(brokerToConnect);
                this.rendezvousList.add(rdvInstance);
            } else {
                rendezvousList.remove(rdvInstance);
            }
        }
        
        try {
            return rdvInstance.connect();
        } catch (Exception e) {
            rendezvousList.remove(rdvInstance);
            return null;
        }
    }
    
    public Task getCurrentTask() {
        return (Task) Thread.currentThread();
    }
    
    public String getBrokerName() {
        return this.brokerName;
    }
    
    public List<Rdv> getRendezvousList() {
        return rendezvousList;
    }
}
