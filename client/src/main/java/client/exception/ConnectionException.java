package client.exception;

public class ConnectionException extends DataAccessException {
	public ConnectionException(String msg) {
		super(msg);
	}
	public ConnectionException(String msg, Throwable th) {
		super(msg, th);
	}
}

