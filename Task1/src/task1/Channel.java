package task1;

import exception.DisconnectedException;
import task1Interface.IChannel;

public class Channel implements IChannel {
	int port;
	CircularBuffer bufferOut;
	CircularBuffer bufferIn;
	boolean isDisconnect = false;
	Channel rmChannel;
	boolean dangling;

	public Channel(CircularBuffer in) {
		this.bufferIn = in; 
	}
 
	public void setRmChannel(Channel rmChannel) {
		this.rmChannel = rmChannel;
		this.bufferOut = rmChannel.bufferIn;
		rmChannel.bufferOut = this.bufferIn;
		this.dangling = this.rmChannel.disconnected();
		}
	
	@Override
	public int read(byte[] bytes, int offset, int length) throws DisconnectedException {
		int cpt = 0; // Nb des bytes lus
		if (disconnected()) {
			throw new DisconnectedException("La connection a été coupée.");
		}
		if(bufferIn.empty()) {
			synchronized(bufferIn) {
				while(bufferIn.empty()) {
					if (disconnected()) {
						throw new DisconnectedException("La connection a été coupée.");
					}
					try {
						bufferIn.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		while (!bufferIn.empty() && cpt != length) {
			if (!disconnected()) {
				bytes[offset + cpt] = bufferIn.pull();
				cpt += 1;
			} else {
				throw new DisconnectedException("La connection a été coupée.");
			}
		}
		
		if(cpt != 0) {
			synchronized(bufferIn) {
				bufferIn.notifyAll();
			}
		}
		return cpt;
	}

	@Override
	public int write(byte[] bytes, int offset, int length) throws DisconnectedException {
		int cpt = 0; // Nb des bytes écrits
		if (disconnected()) {
			throw new DisconnectedException("La connection a été coupée. Veuillez vous déconnecter");
		}
		if(dangling) {
			disconnect();
		}
		if(bufferOut.full()) {
			synchronized(bufferOut) {
				while(bufferOut.full()) {
					if (disconnected()) {
						throw new DisconnectedException("La connection a été coupée.");
					}
					if(dangling) {
						disconnect();
					}
					try {
						bufferOut.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		while (!bufferOut.full() && cpt != length) {
			if (!disconnected()) {
				bufferOut.push(bytes[offset + cpt]);
				cpt += 1;
			} else {
				throw new DisconnectedException("La connection a été coupée. Veuillez vous déconnecter");
			}
		}
		
		if(cpt != 0) {
			synchronized(bufferOut) {
				bufferOut.notifyAll();
			}
		}
		return cpt;
	}

	@Override
	public void disconnect() {
		if(isDisconnect) {
			return;
		}
		
		this.isDisconnect = true;
		if(this.rmChannel != null) {
			this.rmChannel.dangling = true;
		}
		
		synchronized(bufferIn) {
			bufferIn.notifyAll();
		}
		
		synchronized(bufferOut) {
			bufferOut.notifyAll();
		}
	}

	@Override
	public boolean disconnected() {
		return isDisconnect;
	}

}
