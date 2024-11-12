package fullEvent.impl;

import java.util.HashMap;

import event.TaskEvent;
import fullEvent.abst.BrokerFullAbstract;

public class BrokerFull implements BrokerFullAbstract{
	String name;
	HashMap<Integer, RendezVous> rdV = new HashMap<Integer, RendezVous>();
	
	public BrokerFull(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}

	@Override
	public boolean bind(int port, IBrokerAcceptListener listener) {
		if(rdV.get(port)!= null) {
			System.out.println("Port is already exists");
			return false;
		}
		RendezVous rdv = new RendezVous(port);
		rdV.put(port, rdv);
		ChannelFull channel = rdv.accept(this);
		TaskEvent task = new TaskEvent();
		task.post(() -> {
			listener.accepted(channel);
		});
		return true;
	}

	@Override
	public boolean unbind(int port) {
		if(rdV.get(port)== null) {
			System.out.println("Port doesn't exists");
			return false;
		}
		rdV.remove(port);
		return true;
	}

	@Override
	public boolean connect(String name, int port, IBrokerConnectListener listener) {
		BrokerFull b = BrokerFullManager.getSelf().getBroker(name);
		if(b == null) {
			return false;
		}
		
		RendezVous rdv = b.rdV.get(port);
		if(rdv == null) {
			TaskEvent task = new TaskEvent();
			task.post(() -> {
				listener.refused();
			});
			return false;
		}
		
		ChannelFull channel = rdv.connect(this);
		if(channel == null) {
			TaskEvent task = new TaskEvent();
			task.post(() -> {
				listener.refused();
			});
			return false;
		} else {
			TaskEvent task = new TaskEvent();
			task.post(() -> {
				listener.connected(channel);
			});
			return true;
		}
	}
	
	public void clearRdv(int port) {
		this.rdV.get(port).clearPort();
	}

}
