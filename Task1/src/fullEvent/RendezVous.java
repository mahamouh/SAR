package fullEvent;

import threaded.impl.CircularBuffer;

public class RendezVous {
	int port;

	BrokerFull brokerAccept;
	BrokerFull brokerConnect;

	ChannelFull channelAccept;
	ChannelFull channelConnect;

	CircularBuffer bufferAC; // de accpet à connect
	CircularBuffer bufferCA; // de connect à accept

	public RendezVous(int port) {
		this.port = port;
	}

	public int getPort() {
		return port;
	}


	public ChannelFull accept(BrokerFull brokerAccept) {
		this.brokerAccept = brokerAccept;

		bufferAC = new CircularBuffer(256);
		channelAccept = new ChannelFull(bufferAC, port);

		return channelAccept;

	}

	public ChannelFull connect(BrokerFull brokerConnect) {
		if (this.brokerConnect != null) {
			return null;
		} 
		
		this.brokerConnect = brokerConnect;
		
		bufferCA = new CircularBuffer(256);
		channelConnect = new ChannelFull(bufferCA, port);

		channelAccept.setRmChannel(channelConnect);
		channelConnect.setRmChannel(channelAccept);
		
		return channelConnect;

	}
	
	public void clearPort() {
		this.brokerConnect = null;
		this.channelConnect = null;
	
	}

}
