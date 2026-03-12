package dataaccess;

public class AlreadyTakenException extends Exception {
	public AlreadyTakenException(String msg) {
		super(msg);
	}

	public AlreadyTakenException(String msg, Throwable th) {
		super(msg, th);
	}
}
