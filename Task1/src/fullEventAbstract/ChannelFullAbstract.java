package fullEventAbstract;

import event.Message;
import exception.DisconnectedException;

public interface ChannelFullAbstract {
    public interface IChannelListener {
        void wrote(byte[] bytes);
        void disconnected();
        void read(Message msg);
        void availaible();
    }
    
    public abstract void setListener(IChannelListener l);
    
	int read(byte[] bytes, int offset, int length) throws DisconnectedException;

	int write(byte[] bytes, int offset, int length) throws DisconnectedException;

	void disconnect();

	boolean disconnected();
}
