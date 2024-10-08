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
		while (true) {
			task = queue.remove(0);
			while (task != null) {
				task.getRunnable().run();
				task = queue.remove(0);
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
