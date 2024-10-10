package task1;

import java.util.HashMap;
import java.util.LinkedList;

import task1Interface.*;

public class Broker implements IBroker {
	String name;
	private HashMap<Integer, RdV> rdV;
	BrokerManagement brokerManagement;

	public Broker(String name) {
		this.name = name;
		this.rdV = new HashMap<Integer, RdV>();
		this.brokerManagement = BrokerManagement.getSelf();
		this.brokerManagement.addBroker(this);
		
	}

	public void setBroker(BrokerManagement brokerManagement) {
		this.brokerManagement = brokerManagement;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void removePort(int port) {
		RdV rdv = null;
		synchronized (rdV) {
			rdv = rdV.get(port);
			if(rdv != null) {
				rdV.remove(port);
			}
			rdV.notifyAll();
		}
	}


	@Override
	public Channel accept(int port) {
		RdV rdv = null;
		synchronized (rdV) {
			rdv = rdV.get(port);
			if(rdv != null) {
				throw new IllegalStateException("Port " + port + " dejà present");
			}
			rdv = new RdV(port);
			this.rdV.put(port, rdv);
			rdV.notifyAll();
		}
		return rdv.accept(this);
	}

	@Override
	public Channel connect(String name, int port) {
		Broker b = this.brokerManagement.getBroker(name);
		if(b == null) {
			return null;
		} 
		return b.connectBroker(this, port);
		
	}
	
	private Channel connectBroker(Broker b, int port) {
		RdV rdv = null;
		synchronized (rdV) {
			rdv = rdV.get(port);
			while(rdv == null) {
				try {
					rdV.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				rdv = rdV.get(port);
			}
			this.rdV.remove(port);
		}
		return rdv.connect(this);
	}

}
