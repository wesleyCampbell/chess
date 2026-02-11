package chess;

/**
 * Indicates that an invalid board state was passed into the generator
 */
public class InvalidBoardStateException extends Exception {

	public InvalidBoardStateException() {}

	public InvalidBoardStateException(String msg) {
		super(msg);
	}
}
