package client.exception;

public class AuthenticationException extends DataAccessException {
	public AuthenticationException(String msg) {
		super(msg);
	}
	public AuthenticationException(String msg, Throwable th) {
		super(msg, th);
	}
}
