package fullEvent.impl;

import exception.DisconnectedException;
import threaded.impl.CircularBuffer;

public class Reader {
	// status to know where you are during reading
	private enum State {
		READING_LENGTH, READING_MSG
	};

	private State state;
	CircularBuffer bufferIn;
	ChannelFull channel;

	public Reader(ChannelFull channel) {
		state = State.READING_LENGTH;
		this.bufferIn = channel.bufferIn;
		this.channel = channel;
	}

	public byte[] handleRead() throws DisconnectedException {
		byte[] data;
		int len = 0;
		switch (this.state) {

		case READING_LENGTH:
			if (channel.disconnected()) {
				throw new DisconnectedException("La connection a été coupée.");
			}
			if (!this.bufferIn.empty()) {
				byte[] sizeBuffer = new byte[4];
				if (!channel.disconnected()) {
					channel.readLen(sizeBuffer);
					len = bytesToInt(sizeBuffer);
				} else {
					throw new DisconnectedException("La connection a été coupée.");
				}
				this.state = State.READING_MSG;
			}

		case READING_MSG:
			if (channel.disconnected()) {
				throw new DisconnectedException("La connection a été coupée.");
			}
			if (!this.bufferIn.empty()) {
				data = new byte[len];
				int cpt = 0;
				while(cpt != len) {
					if (!channel.disconnected()) {
						cpt += channel.read(data, cpt, len-cpt);
					} else {
						throw new DisconnectedException("La connection a été coupée.");
					}
				}
				this.state = State.READING_LENGTH;
				return data;
			}
		default:
			break;
		}
		return null;
	}

	private int bytesToInt(byte[] bytes) {
		return ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
	}
}
