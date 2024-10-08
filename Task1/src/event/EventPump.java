package event;

import java.util.LinkedList;

public class EventPump {
	LinkedList<Runnable> queue;
	static EventPump instance;

	private EventPump() {
		queue = new LinkedList<Runnable>();
	}

	static {
		instance = new EventPump();
	}

	public static EventPump getSelf() {
		return instance;
	}

	public synchronized void run() {
		Runnable r;
		while (true) {
			r = queue.remove(0);
			while (r != null) {
				r.run();
				r = queue.remove(0);
			}
			sleep();
		}
	}

	public synchronized void post(Runnable r) {
		queue.add(r);
		notify();
	}

	private void sleep() {
		try {
			wait();
		} catch (InterruptedException ex) {
		}
	}
}
