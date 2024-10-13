package event;

import eventAbstract.MessageQueueEventAbstract.IListener;

public class ReceivedTask extends TaskEvent {

	public ReceivedTask(IListener listener, IListener rmListener, byte[] bytes, int offset, int length) {
		super();
		this.runnable = new Runnable() {
			@Override
			public void run() {
				Message msg = new Message(bytes, offset, length);
				listener.sent(msg);

				rmListener.received(bytes);
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
