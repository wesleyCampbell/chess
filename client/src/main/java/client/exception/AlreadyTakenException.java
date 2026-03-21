package client.exception;

public class AlreadyTakenException extends DataAccessException {
	public AlreadyTakenException(String msg) {
		super(msg);
	}
	public AlreadyTakenException(String msg, Throwable th) {
		super(msg, th);
	}
}
