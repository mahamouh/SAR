package mixed.abst;
public abstract class Task{
	
	public abstract void post(Runnable r);
    public static Task task() {
        return null;
    }
    public abstract void kill();
    public abstract boolean killed();
}