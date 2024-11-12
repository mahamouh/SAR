package exception;

public class DisconnectedException extends Exception{

	private static final long serialVersionUID = 1L;
	public DisconnectedException(String exception) {
		super(exception);
	}
}
