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

	@Override
	public void post(Runnable r) {
		throw new IllegalStateException("Cette méthode ne peut pas être appelé ici");
	}

	public void postTask() {
		super.postTask();
	}
	
	
	public Runnable getRunnable() {
		return super.getRunnable();
	}
}
