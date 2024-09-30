package task1Interface;

import exception.DisconnectedException;

public interface IChannel {
	int read(byte[] bytes, int offset, int length) throws DisconnectedException;

	int write(byte[] bytes, int offset, int length) throws DisconnectedException;

	void disconnect();

	boolean disconnected();
}
