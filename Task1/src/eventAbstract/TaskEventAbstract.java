package eventAbstract;

public abstract class TaskEventAbstract {
	public abstract void post(Runnable r);
	public abstract void kill();
	public abstract boolean killed();
}
