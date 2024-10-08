package event;

public class Message {
	byte[] bytes;
	int offset;
	int length;
	
	public Message(byte[] bytes, int offset, int length) {
		this.bytes = bytes;
		this.offset = offset;
		this.length = length;
	}
}
