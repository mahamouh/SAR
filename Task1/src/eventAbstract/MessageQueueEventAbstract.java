package eventAbstract;

import event.Message;
import task1.Channel;

public abstract class MessageQueueEventAbstract {
	Channel channel;
	
	public MessageQueueEventAbstract(Channel channel) {
		this.channel = channel;
	}
	
    public interface IListener {
        void received(byte[] msg);
        void closed();
        void sent(Message msg);
    }
    
    public abstract void setListener(IListener l);
    
    public abstract boolean send(byte[] bytes);
    public abstract boolean send(byte[] bytes, int offset, int length);
    
    public abstract void close();
    public abstract boolean closed();
}
