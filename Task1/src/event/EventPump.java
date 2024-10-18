package event;

import java.util.LinkedList;

public class EventPump {
	LinkedList<TaskEvent> queue;
	static EventPump instance;
	private Boolean killed = false;

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
		while(!killed) {
			if (queue.isEmpty()) {
				sleep();
			} else {
				task = queue.remove(0);
				if (task != null) {
					task.getRunnable().run();
					task.kill();
				}
			}
		}
		
	}
	
	public void kill() {
		Thread.currentThread().interrupt();
	}

	public synchronized void post(TaskEvent task) {
		queue.addLast(task);
		notify();
	}

	private void sleep() {
		try {
			wait();
		} catch (InterruptedException ex) {
		}
	}
}
