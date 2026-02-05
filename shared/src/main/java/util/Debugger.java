package util;

public class Debugger {
	public static void debug(String msg, int level) {
		StringBuilder outStr = new StringBuilder();

		for (int i = 0; i < level; i++) {
			outStr.append("\t");
		}

		outStr.append("DEBUG: ");
		outStr.append(msg);

		System.out.println(outStr.toString());
	}
}
