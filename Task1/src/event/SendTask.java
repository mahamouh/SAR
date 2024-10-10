package event;

public class SendTask extends TaskEvent{

	public SendTask() {
		super();
		this.runnable = new Runnable () {
			@Override
			public void run() {
				
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
