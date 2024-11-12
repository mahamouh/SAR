package mixed.impl;


import java.util.LinkedList;
import java.util.Queue;


public class EventPump extends Thread {

	private static final EventPump INSTANCE = new EventPump();
	private static Runnable currentRunnable;
	
	private boolean isRunning;
	private Queue<Runnable> runnableQueue;

	private EventPump() {
		runnableQueue = new LinkedList<>();
		isRunning = true;
	}

	public static EventPump getInstance() {
		return INSTANCE;
	}

	synchronized public void post(Runnable runnable) {
		runnableQueue.add(runnable);
		notify();
	}

	synchronized public void unpost(Runnable runnable) {
		runnableQueue.remove(runnable);
	}

	public static Runnable getCurrentRunnable() {
		return currentRunnable;
	}

	synchronized public void stopPump() {
		isRunning = false;
		notifyAll();
	}

	@Override
	public void run() {
		while (isRunning) {
			while (isRunnableQueueEmpty() && isRunning) {
				synchronized (this) {
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
						return;
					}
				}
			}

			if (!isRunning) {
				return;
			}

			currentRunnable = getNextRunnable();
			if (currentRunnable != null) {
				try {
					currentRunnable.run();
				} catch (Exception e) {
					// Handle exception
				}
			}
		}
	}

	synchronized private Runnable getNextRunnable() {
		return runnableQueue.poll();
	}

	synchronized private boolean isRunnableQueueEmpty() {
		return runnableQueue.isEmpty();
	}
}
