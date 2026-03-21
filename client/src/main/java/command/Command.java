package command;

import java.util.List;

public interface Command {
	/**
	 * Returns the string key that coorelates with the command
	 *
	 * @return String
	 */
	public String getCommandStr();

	/**
	 * Returns a string description of the command
	 *
	 * @return String
	 */
	public String getDescription();

	/**
	 * Prints the usage of the command.
	 */
	public void printUsage();

	/**
	 * Executes the command on a given application
	 *
	 * @param app The application on which to run the command
	 *
	 * @return bool True if successfull, false otherwise
	 */
	public boolean executeCommand(List<String> parameters);
}
