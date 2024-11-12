package threaded.impl;

public class Task extends threaded.abst.Task{
	
	private Broker br;
	private Runnable r;

	public Task(Broker b, Runnable r) {
		super(b, r);
		this.br = b;
		this.r = r;
	}

	@Override
	public Broker getBroker() {
		return this.br;
	}

	@Override
	public void run() {
		this.r.run();
	}

}