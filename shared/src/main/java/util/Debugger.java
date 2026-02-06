package util;

public class Debugger {
	/**
	 * Will print a provided debug message at a given indent level
	 *    '    DEBUG: ...'
	 *
	 * @param msg The message to print
	 * @level How many indents to include
	 */
	public static void debug(String msg, int level) {
		StringBuilder outStr = new StringBuilder();

		for (int i = 0; i < level; i++) {
			outStr.append("\t");
		}

		outStr.append("DEBUG: ");
		outStr.append(msg);

		System.out.println(outStr.toString());
	}

	/**
	 * Prints out a debug message in the format:
	 *    'DEBUG: ...'
	 *
	 * @param msg The message to print
	 */
	public static void debug(String msg) {
		System.out.println("DEBUG: " + msg);
	}
}
