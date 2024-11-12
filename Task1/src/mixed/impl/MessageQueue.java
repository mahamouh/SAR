package mixed.impl;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;


import mixed.abst.MessageQueue.Listener;
import threaded.impl.Channel;
public class MessageQueue extends mixed.abst.MessageQueue {
	private boolean closed;
	private Channel channel;
	private Listener listener;
	private Queue<Message> messages;
	private Thread sendThread;

	public MessageQueue(Channel channel) {
		super();
		this.channel = channel;
		this.closed = false;
		this.messages = new LinkedList<Message>();
		startSendThread();
		_receive();
	}

	@Override
	public void setListener(Listener listener) {
		this.listener = listener;
	}

	public void send(Message msg) {
		synchronized (sendThread) {
			messages.add(msg);
			sendThread.notify();
		}
	}

	private void startSendThread() {
		sendThread = new Thread(() -> {
			while (!closed) {
				processMessages();
			}
		});
		sendThread.start();
	}

	private void processMessages() {
		while (messages.isEmpty() && !closed()) {
			waitForMessage();
		}

		Message msg = messages.poll();
		if (msg != null) {
			_send(msg);
		}
	}

	private void waitForMessage() {
		try {
			synchronized (sendThread) {
				sendThread.wait();
			}
		} catch (InterruptedException e) {
			return; // Channel closed
		}
	}

	private void _send(Message msg) {
		sendMessageLength(msg);
		sendMessageContent(msg);
		Task task = new Task();
		task.post(() -> listener.sent(msg));
	}

	private void sendMessageLength(Message msg) {
		int length = msg.getLength();
		byte[] size = new byte[4];
		size[0] = (byte) ((length & 0xFF000000) >> 24);
		size[1] = (byte) ((length & 0x00FF0000) >> 16);
		size[2] = (byte) ((length & 0x0000FF00) >> 8);
		size[3] = (byte) (length & 0x000000FF);

		int nbSentBytes = 0;
		while (nbSentBytes < 4) {
			try {
				nbSentBytes += channel.write(size, nbSentBytes, 4 - nbSentBytes);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void sendMessageContent(Message msg) {
		int nbSentBytes = 0;
		int length = msg.getLength();
		while (nbSentBytes < length) {
			try {
				nbSentBytes += channel.write(msg.getBytes(), msg.getOffset() + nbSentBytes, length - nbSentBytes);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void _receive() {
		new Thread(() -> {
			while (!closed()) {
				readMessageSize();
			}
		}).start();
	}

	private void readMessageSize() {
		int nbReceivedBytes = 0;
		byte[] size = new byte[4];

		while (nbReceivedBytes < 4) {
			try {
				nbReceivedBytes += channel.read(size, nbReceivedBytes, 4 - nbReceivedBytes);
			} catch (Exception e) {
				return;
			}
		}

		int length = getMessageLength(size);
		readMessageContent(length);
	}

	private int getMessageLength(byte[] size) {
		return ((size[0] & 0xFF) << 24) |
				((size[1] & 0xFF) << 16) |
				((size[2] & 0xFF) << 8) |
				(size[3] & 0xFF);
	}

	private void readMessageContent(int length) {
		int nbReceivedBytes = 0;
		byte[] message = new byte[length];

		while (nbReceivedBytes < length) {
			try {
				nbReceivedBytes += channel.read(message, nbReceivedBytes, length - nbReceivedBytes);
			} catch (Exception e) {
				return;
			}
		}

		Task task = new Task();
		task.post(() -> listener.received(message));
	}

	@Override
	public void close() {
		channel.disconnect();
		sendThread.interrupt();
	}

	@Override
	public boolean closed() {
		return closed;
	}

	

}