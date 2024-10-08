package task1;

import exception.DisconnectedException;
import task1Interface.IMessageQueue;

public class MessageQueue implements IMessageQueue {
	Channel channel;

	public MessageQueue(Channel channel) {
		this.channel = channel;
	}
 
	@Override
	public void send(byte[] bytes, int offset, int length) throws DisconnectedException {
		if(closed()) {
			throw new DisconnectedException("La connection a été coupée.");
		}
		//On écrit la taille de buffer
		byte[] sizeBuffer = intToBytes(length);
		try {
			this.channel.write(sizeBuffer, 0, sizeBuffer.length);
		} catch (DisconnectedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//On écrit le buffer
		int cpt = 0;
		while (cpt != length) {
			try {
				cpt += this.channel.write(bytes, offset + cpt, length - cpt);
			} catch (DisconnectedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public byte[] receive() throws DisconnectedException {
		if(closed()) {
			throw new DisconnectedException("La connection a été coupée.");
		}
		
		//On lit la taille du buffer
		byte[] sizeBuffer = new byte[4];
		try {
			channel.read(sizeBuffer, 0, sizeBuffer.length);
		} catch (DisconnectedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//On lit le buffer
		
		int size = bytesToInt(sizeBuffer);
		byte[] bytes = new byte[size];
		int cpt = 0;
		try {
			while (cpt != size) {
				cpt += channel.read(bytes, cpt, bytes.length - cpt);
			}
		} catch (DisconnectedException e) {
			System.err.println("Error during receive: " + e.getMessage());
		}
		
		return bytes;
	}

	@Override
	public void close() {
		channel.disconnect();
	}

	@Override
	public boolean closed() {
		return channel.isDisconnect;
	}

	private byte[] intToBytes(int value) {
        return new byte[] {
            (byte) (value >> 24),
            (byte) (value >> 16),
            (byte) (value >> 8),
            (byte) value
        };
    }

    private int bytesToInt(byte[] bytes) {
        return ((bytes[0] & 0xFF) << 24) |
               ((bytes[1] & 0xFF) << 16) |
               ((bytes[2] & 0xFF) << 8)  |
               (bytes[3] & 0xFF);
    }
}
