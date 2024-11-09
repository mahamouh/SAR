package fullEvent;

import event.Message;
import event.TaskEvent;
import exception.DisconnectedException;
import fullEventAbstract.ChannelFullAbstract;
import fullEventAbstract.MessageQueueFullAbstract;

public class MessageQueueFull implements MessageQueueFullAbstract {
	IMessageQueueListener listener;
	ChannelFull channelFull;
	Writer writer;
	Reader reader;

	public MessageQueueFull(ChannelFull channel) {
		this.channelFull = channel;
		writer = new Writer(channel);
		reader = new Reader(channel);
	}

	@Override
	public void setListener(IMessageQueueListener l) {
		listener = l;
		MyEchoClientChannelListener channelListener = new MyEchoClientChannelListener(listener);
		channelFull.setListener(channelListener);
	}

	@Override
	public boolean send(Message msg) {
		writer.sendMsg(msg);
		Message res;
		try {
			res = writer.handleWrite();
			return true;
		} catch (DisconnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public void receive() {
		try {
			byte[] res = reader.handleRead();
		} catch (DisconnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		this.channelFull.disconnect();
	}

	@Override
	public boolean closed() {
		return this.channelFull.disconnected();
	}
	
	public ChannelFull getChannel() {
		return channelFull;
	}
	
	class MyEchoClientChannelListener implements ChannelFullAbstract.IChannelListener {

		IMessageQueueListener listener;
		
		public MyEchoClientChannelListener(IMessageQueueListener listener) {
			this.listener = listener;
		}
	

		@Override
		public void wrote(byte[] bytes) {
			Message msg = new Message(bytes, 0, bytes.length);
			TaskEvent task = new TaskEvent();
			task.post(() -> {
				this.listener.sent(msg);
			});
		}

		@Override
		public void disconnected() {
			TaskEvent task = new TaskEvent();
			task.post(() -> {
				this.listener.closed();
			});
		}

		@Override
		public void read(Message msg) {
			TaskEvent task = new TaskEvent();
			task.post(() -> {
				this.listener.received(msg.getByte());
			});
		}

		@Override
		public void availaible() {
			TaskEvent task = new TaskEvent();
			task.post(() -> {
				this.listener.availaible();
			});
		} 

	}

}
