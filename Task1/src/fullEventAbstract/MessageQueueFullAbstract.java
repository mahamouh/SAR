package fullEventAbstract;

import event.Message;

public interface MessageQueueFullAbstract {
	
    public interface IMessageQueueListener {
        void received(byte[] msg);
        void closed();
        void sent(Message msg);
        void availaible();
    }
    
    public abstract void setListener(IMessageQueueListener l);
    
    public abstract boolean send(Message msg);
    
    public abstract void close();
    public abstract boolean closed();

	public abstract void receive();
}
