package chess;

/**
 * A string representation of a board state used to quickly define various
 * board configurations easily
 */
public class BoardState {
	//
	// ======================== STATIC ATTRIBUTES ===========================
	//
	
	private static String[] DEFAULT_STATE = {
		"RNBQKBNR",
		"PPPPPPPP",
		"--------",
		"--------",
		"--------",
		"--------",
		"PPPPPPPP",
		"RNBQKBNR"
	};

	private static String[] FUNKY_STATE = {
		"BNQPBNKR",
		"PPPPPRPP",
		"--------",
		"--P-----",
		"--------",
		"-----P--",
		"PPPPPPPP",
		"QBNRRBNK"
	};

	public static BoardState DEFAULT = new BoardState();

	//
	// ======================== STATIC METHODS =========================
	//
	
	/**
	 * Verifies the validity of a game state.
	 *
	 * Currently only looks at dimentions
	 */
	public static boolean validateBoardState(BoardState state) {
		int colNum = state.getState().length;

		for (int i = 1; i < state.getState().length; i++) {
			if (state.getState()[i].length() != colNum) {
				return false;
			}
		}

		return true;

	}

	//
	// ======================= MEMBER ATTRIBUTES ==============
	//
	
	private String[] state;

	//
	// ======================= CONSTRUCTORS ====================
	//
	
	/**
	 * Default Constructor. Assigns the state to the default.
	 */
	public BoardState() {
		this.state = DEFAULT_STATE;
	}

	/**
	 * Constructor
	 *
	 * @param state: A string array representing the game-state
	 */
	public BoardState(String[] state) {
		this.state = state;
	}

	/**
	 * Getter for board's state
	 *
	 * @return String[]: the state
	 */
	public String[] getState() {
		return this.state;
	}

}
