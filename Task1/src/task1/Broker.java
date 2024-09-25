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

	public synchronized void addRdV(int port) {
		RdV rdv = new RdV(port);
		this.rdV.put(port, rdv);
	}

	public synchronized void removeRdV(int port) {
		this.rdV.remove(port);
	}

	public RdV containsRdV(int port) {
		RdV rdv = this.rdV.get(port);
		return rdv;
	}

	@Override
	public Channel accept(int port) {
		synchronized (this) {
			RdV rdv = containsRdV(port);
			if (rdv == null) {
				rdv = new RdV(port);
				this.rdV.put(port, rdv);
			} 
			return rdv.accept(this);
		}
	}

	@Override
	public Channel connect(String name, int port) {
		synchronized(this) {
			RdV rdv = containsRdV(port);
			if (rdv == null) {
				rdv = new RdV(port);
				this.rdV.put(port, rdv);
				if(brokerManagement.getBroker(name) !=null) {
					brokerManagement.getBroker(name).addRdV(port);
				}
			}
			return rdv.connect(this);
		}
	}

}
