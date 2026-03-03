package dataaccess;

public class AuthenticationException extends Exception {
	public AuthenticationException(String msg) {
		super(msg); 
	}

	public AuthenticationException(String msg, Throwable th) {
		super(msg, th);
	}
}
