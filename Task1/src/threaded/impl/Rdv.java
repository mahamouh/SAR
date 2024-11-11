package threaded.impl;


public class Rdv {

	private int portNumber;
	private Broker acceptBroker, connectBroker;
	private Channel acceptChannel, connectChannel;
	
	public Rdv(int port) {
		this.portNumber = port;
	}
	
	synchronized public Channel connect() {
				
		if(this.acceptBroker != null) {
			this.createNewChannels();
			
			notifyAll();
		} else {
			try {
				while(this.acceptBroker == null) {
					wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return this.connectChannel;
	}
	
	synchronized public Channel accept() {
				
		if(this.connectBroker != null) {
			this.createNewChannels();
			
			notifyAll();
			
		} else {
			try {
				while(connectBroker == null) {
					wait();
				}
			} catch (InterruptedException e) {
			}
		}
		return this.acceptChannel;
	}
	
	private void createNewChannels() {
		
		this.acceptChannel = new Channel();
		this.connectChannel = new Channel();
		
		this.acceptChannel.outputBuffer = this.connectChannel.inputBuffer;
		this.connectChannel.outputBuffer = this.acceptChannel.inputBuffer;
		
		this.acceptChannel.remoteChannel = this.connectChannel;
		this.connectChannel.remoteChannel = this.acceptChannel;
	}
	
	public int getPort() {
		return this.portNumber;
	}
	
	public boolean isAccept() {
		return this.acceptBroker != null;
	}
	
	public boolean isConnect() {
		return this.connectBroker != null;
	}
	
	public void setConnectBroker(Broker b) {
		this.connectBroker = b;
	}
	
	public void setAcceptBroker(Broker b) {
		this.acceptBroker = b;
	}
}

