package fullEvent;

import exception.DisconnectedException;
import fullEventAbstract.ChannelFullAbstract;
import task1.CircularBuffer;

import java.util.LinkedList;

import event.Message;
import event.TaskEvent;

public class ChannelFull implements ChannelFullAbstract {
	CircularBuffer bufferIn;
	CircularBuffer bufferOut;
	ChannelFull rmChannel;
	LinkedList<Message> queue = new LinkedList<Message>();
	IChannelListener listener;
	private boolean disconnected = false;
	boolean dangling;

	public ChannelFull(CircularBuffer in) {
		this.bufferIn = in;
	}

	public void setRmChannel(ChannelFull rmChannel) {
		this.rmChannel = rmChannel;
		this.dangling = this.rmChannel.disconnected;
		this.bufferOut = this.rmChannel.bufferIn;
		this.rmChannel.bufferOut = this.bufferIn;
	}

	@Override
	public void setListener(IChannelListener l) {
		this.listener = l;
	}

	@Override
	public int read(byte[] bytes, int offset, int length) throws DisconnectedException {
		if (disconnected) {
			throw new DisconnectedException("La connection a été coupée.");
		}
		int cpt = 0;
		if (!this.bufferIn.empty()) {
			while (!bufferIn.empty() && cpt != length) {
				if (!disconnected) {
					bytes[cpt] = bufferIn.pull();
					cpt += 1;
				} else {
					throw new DisconnectedException("La connection a été coupée.");
				}
			}

		}
		if (cpt == length) {
			Message msg = new Message(bytes, offset, length);
			TaskEvent task = new TaskEvent();
			task.post(() -> {
				listener.read(msg);
			});

		}
		return cpt;
	}

	@Override
	public int write(byte[] bytes, int offset, int length) throws DisconnectedException {
		if (disconnected || dangling) {
			if (dangling) {
				disconnect();
			}
			throw new DisconnectedException("The connexion has been broken");
		}
		int cpt = 0;
		while (!bufferOut.full() && cpt != length) {
			if (!disconnected && !dangling) {
				bufferOut.push(bytes[cpt]);
				cpt += 1;
			} else if (dangling) {
				disconnect();
			} else {
				throw new DisconnectedException("The connexion has been broken");
			}
		}
		if (cpt == length) {
			Message msg = new Message(bytes, offset, length);
			TaskEvent task = new TaskEvent();
			task.post(() -> {
				listener.wrote(bytes);
			});
		}
		return cpt;
	}

	@Override
	public void disconnect() {
		this.disconnected = true;
		this.rmChannel.dangling = true;
		TaskEvent task = new TaskEvent();
		task.post(() -> {
			this.listener.disconnected();
		});
	}

	@Override
	public boolean disconnected() {
		return this.disconnected;
	}

}
