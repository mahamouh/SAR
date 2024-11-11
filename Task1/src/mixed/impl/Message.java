package mixed.impl;


public class Message {

	private byte[] bytesArray;
	private int offset;
	private int length;

	public Message(byte[] bytes, int offset, int length) {
		this.bytesArray = bytes;
		this.offset = offset;
		this.length = length;
	}

	public Message(byte[] bytes) {
		this(bytes, 0, bytes.length);
	}

	public Message(int length) {
		this.bytesArray = new byte[length];
		this.offset = 0;
		this.length = length;
	}

	public int getOffset() {
		return offset;
	}

	public int getLength() {
		return length;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public byte[] getBytes() {
		return bytesArray;
	}

	public byte getByteAt(int index) {
		return bytesArray[index];
	}

	public void setByteAt(byte value, int index) {
		bytesArray[index] = value;
	}

	@Override
	public String toString() {
		return new String(bytesArray);
	}
}
