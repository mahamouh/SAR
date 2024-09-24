package task1;

import task1Interface.ITask;

public class Task extends Thread implements ITask{
	Broker broker;
	Runnable runnable;
	
	public Task(Broker b, Runnable r) {
		this.broker = b;
		this.runnable = r;
	}
	
	@Override
	public void run() {
		if(this.runnable != null) {
			this.runnable.run();
		}
	}

	@Override
	public Broker getBroker() {
		return this.broker;
	}

}
