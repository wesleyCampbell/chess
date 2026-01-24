package chess;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

	//
	// ======================== MEMBER ATTRIBUTES =======================
	//
	
	private int row;
	private int col;
	
	//
	// ======================== CONSTRUCTORS =============================
	//

	/**
	 * Constructor for the ChessPosition.
	 *
	 * @param row: the row 
	 * @param col: the column
	 */
    public ChessPosition(int row, int col) {
		this.row = row;
		this.col = col;
    }

	/**
	 * Copy constructor for ChessPosition.
	 *
	 * @param other: The ChessPosition to copy
	 */
	public ChessPosition(ChessPosition other) {
		this.col = other.getColumn();
		this.row = other.getRow();
	}

	//
	// ======================== MEMBER METHODS ===========================
	//
	
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

	/**
	 * Allows two ChessPositions to be added together
	 *
	 * @param addPosition The position to add onto this position
	 */
	public void add(ChessPosition addPosition) {
		this.col += addPosition.getColumn();
		this.row += addPosition.getRow();
	}

	/**
	 * Allows a row and column value to be added on to itself 
	 *
	 * @param rowValue The value to add to the row
	 * @param colValue The value to add to the column
	 */
	public void add(int rowValue, int colValue) {
		this.col += rowValue; 
		this.row += colValue;
	}

	/**
	 * Allows for a form of vector multiplication.
	 *
	 * Will multiply the row values together and the column values together.
	 *
	 * @param multPosition The vector to multiply
	 */
	public void multiply(ChessPosition multPosition) {
		this.row *= multPosition.getRow();
		this.col *= multPosition.getColumn();
	}	

	/**
	 * Overriden equality function.
	 *
	 * Will determine equality based on another ChessPosition's row and col value.
	 *
	 * @param obj: The other ChessPiece
	 * @return true if equal, false otherwise
	 */
	@Override
	public boolean equals(Object obj) {
		// obj must exist and must be of type ChessPosition
		if (obj == null || obj.getClass() != ChessPosition.class) {
			return false;
		}

		ChessPosition other = (ChessPosition)obj;

		if (this.row != other.getRow() || 
			this.col != other.getColumn()) {
			return false;
		}

		return true;
	}
	
	/**
	 * Overriden hashCode function.
	 *
	 * Will determin a hash code determined by its row and column value 
	 */
	@Override
	public int hashCode() {
		int hash = this.row * 31;
		hash += this.col;

		return hash;
	}

	/**
	 * Provides a string representation of a chess position
	 *
	 * @return String
	 */
	@Override
	public String toString() {
		return String.format("(%d, %d)", this.col, this.row);
	}
}
