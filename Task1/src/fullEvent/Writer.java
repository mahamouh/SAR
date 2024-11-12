package fullEvent;

import java.util.LinkedList;

import event.Message;
import exception.DisconnectedException;
import threaded.impl.CircularBuffer;

public class Writer {

	// status to know where you are during reading
	private enum State {
		WRITING_LENGTH, WRITING_MSG, WRITING_IDLE
	};

	private State state;
	CircularBuffer bufferOut;
	LinkedList<Message> pendingMsgs = new LinkedList<Message>();
	Message msg;
	ChannelFull channel; 

	public Writer(ChannelFull channel) {
		state = State.WRITING_IDLE;
		this.bufferOut = channel.bufferOut;
		this.channel = channel;
	}

	public void sendMsg(Message msg) {
		this.pendingMsgs.addLast(msg);
	}

	public Message handleWrite() throws DisconnectedException {
		int len = 0;
		int cpt = 0;
		switch (this.state) {
		case WRITING_IDLE:
			if (pendingMsgs.size() > 0) {
				if (channel.disconnected()) {
					throw new DisconnectedException("La connection a été coupée.");
				}
				this.msg = this.pendingMsgs.getFirst();
				len = msg.getLength();
				this.state = State.WRITING_LENGTH;
			}
		case WRITING_LENGTH:
			if (channel.disconnected()) {
				throw new DisconnectedException("La connection a été coupée.");
			}
			byte[] sizeMsg = intToBytes(len);
			if (!bufferOut.full()) {
				if (!channel.disconnected()) {
					channel.writeLen(sizeMsg);
				} else {
					throw new DisconnectedException("La connection a été coupée. Veuillez vous déconnecter");
				}
			}
			this.state = State.WRITING_MSG;

		case WRITING_MSG:
			if (channel.disconnected()) {
				throw new DisconnectedException("La connection a été coupée.");
			}
			cpt = msg.getOffset();
			while (!bufferOut.full() && cpt != len) {
				if (!channel.disconnected()) {
					cpt += channel.write(msg.getByte(), cpt, len-cpt);
				} else {
					throw new DisconnectedException("La connection a été coupée. Veuillez vous déconnecter");
				}
			}
			this.state = State.WRITING_IDLE;

		default:
			break;
		}
		return msg;
	}

	private byte[] intToBytes(int value) {
		return new byte[] { (byte) (value >> 24), (byte) (value >> 16), (byte) (value >> 8), (byte) value };
	}
}
