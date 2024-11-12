package threaded.impl;

import threaded.DisconnectedException;

public class Channel extends threaded.abst.Channel {
	
	public Channel remoteChannel;
	private boolean isDisconnected;
	public boolean isDangling;
	public CircularBuffer inputBuffer, outputBuffer;
	
	public Channel() {
		this.inputBuffer = new CircularBuffer(64);
		this.isDisconnected = false;
		this.isDangling = false;
	}
	
	public int read(byte[] buffer, int offset, int length) throws DisconnectedException {
		if (isDisconnected) {
			throw new DisconnectedException("Channel is locally disconnected");
		}
		int bytesRead = 0;
		try {
			while (bytesRead == 0) {
				if (inputBuffer.empty()) {
					synchronized (inputBuffer) {
						while (inputBuffer.empty()) {
							if (isDangling || isDisconnected)
								throw new DisconnectedException("Channel is remotely disconnected");
							try {
								inputBuffer.wait();
							} catch (InterruptedException e) {
								// Do nothing
							}
						}
					}
				}
				
				while (bytesRead < length && !inputBuffer.empty()) {				
					byte value = inputBuffer.pull();
					buffer[offset + bytesRead] = value;
					bytesRead++;
				}
				
				if (bytesRead != 0) {
					synchronized (inputBuffer) {
						inputBuffer.notify();
					}
				}
			}
			
		} catch (DisconnectedException e) {
			if (!isDisconnected) {
				isDisconnected = true;
				synchronized (outputBuffer) {
					outputBuffer.notifyAll();
				}
			}
			throw e;
		}
		return bytesRead;
	}
	
	public int write(byte[] buffer, int offset, int length) throws DisconnectedException {
		if (isDisconnected) {
			throw new DisconnectedException("Channel is locally disconnected");
		}
		int bytesWritten = 0;
		
		while (bytesWritten == 0) {
			if (outputBuffer.full()) {
				synchronized (outputBuffer) {
					while (outputBuffer.full()) {
						if (isDisconnected)
							throw new DisconnectedException("Channel is remotely disconnected");
						if (isDangling) {
							return length;
						}
						try {
							outputBuffer.wait();
						} catch (InterruptedException e) {
							// Do nothing
						}
					}
				}
			}

			while (bytesWritten < length && !outputBuffer.full()) {
				byte value = buffer[offset + bytesWritten];
				outputBuffer.push(value);
				bytesWritten++;
			}

			if (bytesWritten != 0) {
				synchronized (outputBuffer) {
					outputBuffer.notify();
				}
			}
		}
		
		return bytesWritten;
	}

	@Override
	public boolean disconnected() {
		return this.isDisconnected;
	}
	
	public void disconnect() {
		synchronized(this) {
			if(disconnected())
				return;
			
			isDisconnected = true;
			
			remoteChannel.isDangling = true;
			
			synchronized(outputBuffer) {
				outputBuffer.notifyAll();
			}
			
			synchronized(inputBuffer) {
				inputBuffer.notifyAll();
			}
		}
	}
}
