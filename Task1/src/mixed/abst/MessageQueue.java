package mixed.abst;

import mixed.impl.Message;

public abstract class MessageQueue {
	public interface Listener {
		void received(byte[] bytes);

		void closed();

		void sent(Message message);
	}

	public abstract void setListener(Listener listener);

	public abstract void send(Message message);

	public abstract void close();

	public abstract boolean closed();
}