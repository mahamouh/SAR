package event;

import eventAbstract.MessageQueueEventAbstract;

public class SendTask extends TaskEvent {

	public SendTask(MessageQueueEventAbstract.IListener listener, Message msg) {
		super();
		this.runnable = new Runnable() {
			@Override
			public void run() {
				listener.sent(msg);
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
