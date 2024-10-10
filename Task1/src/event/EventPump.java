package event;

import java.util.LinkedList;

public class EventPump {
	LinkedList<TaskEvent> queue;
	static EventPump instance;

	private EventPump() {
		queue = new LinkedList<TaskEvent>();
	}

	static {
		instance = new EventPump();
	}

	public static EventPump getSelf() {
		return instance;
	}

	public synchronized void run() {
		TaskEvent task;
		if (queue.isEmpty()) {
			sleep();
		}
		while (!queue.isEmpty()) {
			task = queue.remove(0);
			if (task != null) {
				task.getRunnable().run();
			}
			sleep();
		}
	}

	public synchronized void post(TaskEvent task) {
		queue.add(task);
		notify();
	}

	private void sleep() {
		try {
			wait();
		} catch (InterruptedException ex) {
		}
	}
}
