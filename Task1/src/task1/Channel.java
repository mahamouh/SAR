package task1;

import task1Interface.IChannel;

public class Channel implements IChannel{
	public Channel (String name, int port) {
		
	}

	@Override
	public int read(byte[] bytes, int offset, int length) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int write(byte[] bytes, int offset, int length) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean disconnected() {
		// TODO Auto-generated method stub
		return false;
	}

}
