package chess;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {
	private int row;
	private int col;

    public ChessPosition(int row, int col) {
		this.row = row;
		this.col = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
		return this.row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
		return this.col;
    }

	@Override 
	public boolean equals(Object obj) {
		// Object must exist and must be ChessPosition object
		if (obj == null) { return false; }
		if (obj.getClass() != ChessPosition.class) { return false; }

		ChessPosition otherPos = (ChessPosition) obj;
		// If the ChessPosition's column and row values are the same, they are equal
		if (this.row == otherPos.getRow() && this.col == otherPos.getColumn()) {
			return true;
		}
		
		return false;
	}

	@Override
	public int hashCode() {
		return this.col + 31 * this.row;
	}

	@Override
	public String toString() {
		StringBuilder outStr = new StringBuilder();

		outStr.append("(");
		outStr.append(this.row);
		outStr.append(", ");
		outStr.append(this.col);
		outStr.append(")");

		return outStr.toString();
	}
}
