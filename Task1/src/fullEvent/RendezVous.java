package fullEvent;

import task1.CircularBuffer;

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


	public synchronized ChannelFull accept(BrokerFull brokerAccept) {
		this.brokerAccept = brokerAccept;

		bufferAC = new CircularBuffer(256);
		channelAccept = new ChannelFull(bufferAC);

		return channelAccept;

	}

	public synchronized ChannelFull connect(BrokerFull brokerConnect) {
		this.brokerConnect = brokerConnect;
		bufferCA = new CircularBuffer(256);
		channelConnect = new ChannelFull(bufferCA);
		
		if (brokerAccept == null) {
			return null;
		} 
		
		channelConnect.setRmChannel(channelAccept);
		channelAccept.setRmChannel(channelConnect);
		return channelConnect;

	}

}
