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

	public synchronized Channel accept(Broker brokerAccept) {
		this.brokerAccept = brokerAccept;
		
		bufferAC = new CircularBuffer(256);
		bufferCA = new CircularBuffer(256);
		
		System.out.println("Le thread connect est reveillé");
		notifyAll();
		
		while(brokerConnect == null) {
			try {
				System.out.println("Le thread accept est bloqué");
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		brokerConnect.addRdV(port);
		channelAccept = new Channel(bufferAC, bufferCA);
		System.out.println("La connexion a été accepté");
		return channelAccept;

	}

	public synchronized Channel connect(Broker brokerConnect) {
		this.brokerConnect = brokerConnect;
		notifyAll();
		System.out.println("Le thread accept est reveillé");
		
		while(brokerAccept == null) {
			System.out.println("Le thread connect est bloqué");
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		brokerAccept.addRdV(port);
		channelConnect = new Channel(bufferCA, bufferAC);
		System.out.println("La connexion a été accepté");
		return channelConnect;

	}

}
