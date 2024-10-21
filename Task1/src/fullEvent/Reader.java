package fullEvent;

import exception.DisconnectedException;
import task1.CircularBuffer;

public class Reader {
	// status to know where you are during reading
	private enum State {
		READING_LENGTH, READING_MSG
	};

	private State state;
	CircularBuffer bufferIn;
	ChannelFull channel; 

	public Reader(CircularBuffer bufferIn, ChannelFull channel) {
		state = State.READING_LENGTH;
		this.bufferIn = bufferIn;
		this.channel = channel;
	}

	public byte[] handleRead() throws DisconnectedException {
		byte[] data;
		int cpt = 0;
		int len = 0;
		switch (this.state) {

		case READING_LENGTH:
			if (channel.disconnected()) {
				throw new DisconnectedException("La connection a été coupée.");
			}
			if (!this.bufferIn.empty()) {
				byte[] sizeBuffer = new byte[4];
				while (!bufferIn.empty() && cpt != sizeBuffer.length) {
					if (!channel.disconnected()) {
						sizeBuffer[cpt] = bufferIn.pull();
						cpt += 1;
					} else {
						throw new DisconnectedException("La connection a été coupée.");
					}
				}
				cpt = 0;
				len = bytesToInt(sizeBuffer);
				this.state = State.READING_MSG;
			}
			
		case READING_MSG:
			if (channel.disconnected()) {
				throw new DisconnectedException("La connection a été coupée.");
			}
			if (!this.bufferIn.empty()) {
				data = new byte[len];
				while (!bufferIn.empty() && cpt != len) {
					if (!channel.disconnected()) {
						data[cpt] = bufferIn.pull();
						cpt += 1;
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
