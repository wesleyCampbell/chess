package command.exception;

public class InvalidParameterException extends Exception {
	public InvalidParameterException(String msg) {
		super(msg);
	}
	public InvalidParameterException(String msg, Throwable th) {
		super(msg, th);
	}
}
