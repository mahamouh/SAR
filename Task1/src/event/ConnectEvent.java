package event;


import eventAbstract.QueueBrokerEventAbstract.IConnectListener;

public class ConnectEvent extends TaskEvent {
	
	public ConnectEvent(IConnectListener listener, MessageQueueEvent msg) {
		super();
		
		this.runnable = new Runnable () {
			@Override
			public void run() {
				listener.connected(msg);
			}
		};
	}
	
	@Override
	public synchronized void post(Runnable r) {
		throw new IllegalStateException("Cette méthode ne peut pas être appelé ici");
	}

	public synchronized void postTask() {
		super.postTask();
	}
	
	public Runnable getRunnable() {
		return super.getRunnable();
	}

}
