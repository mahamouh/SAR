package event;

import task1.Channel;

public class CloseEvent extends TaskEvent{
	Runnable runnable;

	public CloseEvent(Channel channel) {
		super();
		this.runnable = new Runnable () {
			@Override
			public void run() {
				channel.disconnect();
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

}
