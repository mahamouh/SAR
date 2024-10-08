package event;


import eventAbstract.MessageQueueEventAbstract;
import exception.DisconnectedException;
import task1.Channel;

public class MessageQueueEvent extends MessageQueueEventAbstract{
	private IListener listener;
	private MessageQueueEvent rmMessageQueueEvent;
	private boolean isClosed = false;
	private Channel channel;

	public MessageQueueEvent(Channel channel) {
		super(channel);
	}
	
	void setRmMessageQueueEvent(MessageQueueEvent rmMessageQueueEvent) {
		this.rmMessageQueueEvent = rmMessageQueueEvent;
	}


	public boolean send(byte[] bytes) {
		return send(bytes, 0, bytes.length);
	}

	public boolean send(byte[] bytes, int offset, int length) {
		if (isClosed) {
			System.out.println("MessageQueue est fermé. Le message ne peut pas être envoyé");
			return false;
		}

		// On écrit la taille de buffer
		byte[] sizeBuffer = intToBytes(length);
		try {
			this.channel.write(sizeBuffer, 0, sizeBuffer.length);
		} catch (DisconnectedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// On écrit le buffer
		int cpt = 0;
		while (cpt != length) {
			try {
				cpt += this.channel.write(bytes, offset + cpt, length - cpt);
			} catch (DisconnectedException e) {
				e.printStackTrace();
			}
		}

		
		if (listener != null) {
			Message msg = new Message(bytes, offset, length);
			listener.sent(msg);
		}


		System.out.println("le message a bien été envoyé");
		return true;
	}

	private boolean receive() {
		if (isClosed) {
			System.out.println("MessageQueue est fermé. Le message ne peut pas être lu");
			return false;
		}

		// On lit la taille du buffer
		byte[] sizeBuffer = new byte[4];
		try {
			channel.read(sizeBuffer, 0, sizeBuffer.length);
		} catch (DisconnectedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// On lit le buffer
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

		if (listener != null) {
			listener.received(bytes);
		}

		System.out.println("le message a bien été envoyé");
		return true;
		
	}

	public void close() {
		isClosed = true;
		channel.disconnect();
		if (rmMessageQueueEvent.listener != null) {
			rmMessageQueueEvent.listener.closed();
		}
		System.out.println("MessageQueue est fermé");
	}

	public boolean closed() {
		return isClosed;
	}

	private byte[] intToBytes(int value) {
		return new byte[] { (byte) (value >> 24), (byte) (value >> 16), (byte) (value >> 8), (byte) value };
	}

	private int bytesToInt(byte[] bytes) {
		return ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
	}

	@Override
	public void setListener(IListener l) {
		this.listener = l;	
	}

}
