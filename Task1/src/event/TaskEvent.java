package event;

import eventAbstract.TaskEventAbstract;

public class TaskEvent extends TaskEventAbstract{
	QueueBrokerEvent queueBrokerEvent;
	private static TaskEvent task;
	Runnable runnable;
	boolean killed = false;

	public TaskEvent(QueueBrokerEvent queue, Runnable r) {
		this.queueBrokerEvent = queue;
		this.runnable = r;
		task = this;
	}

	public void post(Runnable r) {
		if (!this.killed) {
			EventPump.getSelf().post(r);
		} else {
			throw new IllegalStateException("La Task a été tuée");
		}

	}

	public static TaskEvent task() {
		return task;
	}

	public void kill() {
		this.killed = true;

	}

	public boolean killed() {
		return this.killed;

	}
}
