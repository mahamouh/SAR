package mixed.impl;


import java.util.LinkedList;
import java.util.List;

public class Task extends mixed.abst.Task {

	private static Task currentTask;
	private boolean isKilled;
	private EventPump eventPump;
	private List<Event> eventList;

	public Task() {
		eventPump = EventPump.getInstance();
		isKilled = false;
		eventList = new LinkedList<>();
	}

	@Override
	public void post(Runnable runnable) {
		Event event = new Event(currentTask, this, runnable);
		eventList.add(event);
		eventPump.post(event);
	}

	public void kill() {
		isKilled = true;
	}

	public boolean killed() {
		return isKilled;
	}

	public static Task getTask() {
		return currentTask;
	}

	public static void setCurrentTask(Task task) {
		currentTask = task;
	}
}
