package event;


import eventAbstract.QueueBrokerEventAbstract.IConnectListener;

public class ConnectEvent extends TaskEvent {
	
	public ConnectEvent(QueueBrokerEvent broker, String name, int port, IConnectListener listener) {
		super();
		
		this.runnable = new Runnable () {
			@Override
			public void run() {
				broker._connect(name, port, listener);
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
