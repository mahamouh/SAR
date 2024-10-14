package event;

import task1.Channel;

public class CloseEvent extends TaskEvent{

	public CloseEvent(Channel channel, MessageQueueEvent.IListener listener) {
		super();
		this.runnable = new Runnable () {
			@Override
			public void run() {
				channel.disconnect();
				listener.closed();
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
