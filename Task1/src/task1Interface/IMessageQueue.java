package task1Interface;

public interface IMessageQueue {
	void send(byte[] bytes, int offset, int length);
	byte[] receive();
	void close();
	boolean closed();
}
