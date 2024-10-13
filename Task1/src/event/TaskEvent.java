package event;

import eventAbstract.TaskEventAbstract;

public class TaskEvent extends TaskEventAbstract {
	private static TaskEvent task;
	Runnable runnable;
	boolean killed = false;

	public TaskEvent() {
		this.runnable = null;
		task = this;
	}

	public synchronized void post(Runnable r) {
		if (!this.killed) {
			this.runnable = r;
			EventPump.getSelf().post(this);
		} else {
			throw new IllegalStateException("La Task a été tuée");
		}

	}

	public synchronized void postTask() {
		if (!this.killed) {
			EventPump.getSelf().post(this);
		} else {
			throw new IllegalStateException("La Task a été tuée");
		}
	}

	public Runnable getRunnable() {
		return this.runnable;
	}

	public static TaskEvent getTask() {
		return task;
	}

	public void kill() {
		this.killed = true;

	}

	public boolean killed() {
		return this.killed;

	}
}
