package task1;

import task1Interface.ITask;

public class Task extends Thread implements ITask{
	Broker broker;
	Runnable runnable;
	QueueBroker queueBroker;
	static private Task task;
	
	
	static Task getTask() {
		return task;
	}
	
	public Task(Broker b, Runnable r) {
		this.broker = b;
		this.runnable = r;
		task = this;
	}
	
	public Task(QueueBroker b, Runnable r) {
		this.queueBroker = b;
		this.broker = b.getBroker();
		this.runnable = r;
		task = this;
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

	@Override
	public QueueBroker getQueueBroker() {
		return this.queueBroker;
	}

}
