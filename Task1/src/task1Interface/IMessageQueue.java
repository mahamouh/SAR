package task1Interface;

import exception.DisconnectedException;

public interface IMessageQueue {
	void send(byte[] bytes, int offset, int length) throws DisconnectedException;
	byte[] receive() throws DisconnectedException;
	void close();
	boolean closed();
}
