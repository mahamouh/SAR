package threaded;

public class DisconnectedException extends Exception {
	
	public DisconnectedException(String s) {
        super("Channel got disconnected.");
    }

}
