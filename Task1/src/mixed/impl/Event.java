package mixed.impl;

public class Event implements Runnable {

	private final Task sourceTask;
	private final Task targetTask;
	private final Runnable taskRunnable;

	public Event(Task sourceTask, Task targetTask, Runnable taskRunnable) {
		this.sourceTask = sourceTask;
		this.targetTask = targetTask;
		this.taskRunnable = taskRunnable;
	}

	@Override
	public void run() {
		if (!targetTask.killed()) {
			Task.setCurrentTask(targetTask);
			taskRunnable.run();
		}
	}
}

