package threaded.abst;

public abstract class Task extends Thread{
	
    protected Task(Broker b, Runnable r){};
	
	public abstract Broker getBroker();

}