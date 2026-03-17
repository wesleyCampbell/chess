package command;

public interface Command {
	/**
	 * Returns the string key that coorelates with the command
	 *
	 * @return String
	 */
	public String getCommandStr();

	/**
	 * Executes the command on a given application
	 *
	 * @param app The application on which to run the command
	 *
	 * @return bool True if successfull, false otherwise
	 */
	public boolean executeCommand();
}
