package fullEvent;

import java.util.LinkedList;

import exception.DisconnectedException;
import task1.CircularBuffer;

public class Writer {

	// status to know where you are during reading
	private enum State {
		WRITING_LENGTH, WRITING_MSG, WRITING_IDLE
	};

	private State state;
	CircularBuffer bufferOut;
	LinkedList<byte[]> pendingMsgs = new LinkedList<byte[]>();
	byte[] msg;
	ChannelFull channel; 

	public Writer(CircularBuffer bufferOut, ChannelFull channel) {
		state = State.WRITING_IDLE;
		this.bufferOut = bufferOut;
		this.channel = channel;
	}

	public void sendMsg(byte[] msg) {
		this.pendingMsgs.addLast(msg);
	}

	public boolean handleWrite() throws DisconnectedException {
		int len = 0;
		int cpt = 0;
		switch (this.state) {
		case WRITING_IDLE:
			if (pendingMsgs.size() > 0) {
				if (channel.disconnected()) {
					throw new DisconnectedException("La connection a été coupée.");
				}
				this.msg = this.pendingMsgs.getFirst();
				len = msg.length;
				this.state = State.WRITING_LENGTH;
			}
		case WRITING_LENGTH:
			if (channel.disconnected()) {
				throw new DisconnectedException("La connection a été coupée.");
			}
			byte[] sizeMsg = intToBytes(len);
			while (!bufferOut.full() && cpt != sizeMsg.length) {
				if (!channel.disconnected()) {
					bufferOut.push(sizeMsg[cpt]);
					cpt += 1;
				} else {
					throw new DisconnectedException("La connection a été coupée. Veuillez vous déconnecter");
				}
			}
			cpt = 0;
			this.state = State.WRITING_MSG;

		case WRITING_MSG:
			if (channel.disconnected()) {
				throw new DisconnectedException("La connection a été coupée.");
			}
			while (!bufferOut.full() && cpt != len) {
				if (!channel.disconnected()) {
					bufferOut.push(msg[cpt]);
					cpt += 1;
				} else {
					throw new DisconnectedException("La connection a été coupée. Veuillez vous déconnecter");
				}
			}
			cpt = 0;
			this.state = State.WRITING_IDLE;

		default:
			break;
		}
		return (pendingMsgs.size() == 0);
	}

	private byte[] intToBytes(int value) {
		return new byte[] { (byte) (value >> 24), (byte) (value >> 16), (byte) (value >> 8), (byte) value };
	}
}
