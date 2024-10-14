package event;

import java.util.Map;

import eventAbstract.QueueBrokerEventAbstract.IAcceptListener;

public class AcceptEvent extends TaskEvent{

	public AcceptEvent(int port, IAcceptListener listener, Map<Integer, IAcceptListener> accepts) {
		super();
		this.runnable = new Runnable () {
			@Override
			public void run() {
				accepts.put(port, listener);
			}
		};
	}
	
	public AcceptEvent(IAcceptListener listener, MessageQueueEvent msg) {
		super();
		this.runnable = new Runnable () {
			@Override
			public void run() {
				listener.accepted(msg);
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
