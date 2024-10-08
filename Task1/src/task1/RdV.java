package task1;

public class RdV {
	int port;

	Broker brokerAccept;
	Broker brokerConnect;

	Channel channelAccept;
	Channel channelConnect;

	CircularBuffer bufferAC; // de accpet à connect
	CircularBuffer bufferCA; // de connect à accept

	public RdV(int port) {
		this.port = port;
	}

	public int getPort() {
		return port;
	}

	public synchronized void waitChannel() {
		if (brokerAccept == null || brokerAccept == null) {
			try {
				System.out.println("Le thread accept est bloqué");
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
 
	public synchronized Channel accept(Broker brokerAccept) {
		this.brokerAccept = brokerAccept;

		bufferAC = new CircularBuffer(256);
		bufferCA = new CircularBuffer(256);
		

		if (brokerConnect == null) {
			waitChannel();
		} else {
			channelAccept = new Channel(bufferAC, bufferCA, channelConnect);
			notifyAll();
		}

		
		return channelAccept;

	}

	public synchronized Channel connect(Broker brokerConnect) {
		this.brokerConnect = brokerConnect;

		if (brokerAccept == null) {
			waitChannel();
		} else {
			channelConnect = new Channel(bufferCA, bufferAC, channelAccept);
			notifyAll();
		}
		
		return channelConnect;

	}

}
