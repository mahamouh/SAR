package event;

import eventAbstract.MessageQueueEventAbstract.IListener;

public class ReceivedTask extends TaskEvent {

	public ReceivedTask(IListener listener, byte[] bytes) {
		super();
		this.runnable = new Runnable() {
			@Override
			public void run() {
				listener.received(bytes);
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
