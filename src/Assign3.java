import java.lang.reflect.Array;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * simple Shell program written in Java
 * @author Danny Clyde
 */

public class Assign3 {
	final static String[] builtInCommands = {"ptime", "history", "list", "cd", "mdir", "rdir"};
	static HashMap<String, ICommandRunner> commandMap = new HashMap<>();
	static Scanner input = new Scanner(System.in);
	public static void main(String[] args) {
		initializeCommandMap();
		String cmd = "";
		while(!cmd.equals("exit")) {
			cmd = getNextLine();
			handleLine(cmd);
		}
	}

	public static void initializeCommandMap() {
		commandMap.put("ptime", (cmd, params) -> //do something);
		commandMap.put("history", HistoryRunner);
		commandMap.put("list", ListRunner);
		commandMap.put("cd", CDRunner);
		commandMap.put("mdir", MDirRunner);
		commandMap.put("rdir", RDirRunner);
	}

	/**
	 * give user prompt (current dir) and wait for their next input
	 * @return the line of user input
	 */
	public static String getNextLine() {
		System.out.print("[" + System.getProperty("user.dir") + "]: ");
		return input.nextLine();
	}

	public static void handleLine(String cmd) {
		String[] splitCmds = splitCommand(cmd);
		if (splitCmds.length < 1) return;
		ArrayList<String> commands = new ArrayList<>();
		ArrayList<String[]> params = new ArrayList<>();
		commands.add(splitCmds[0]);
		int i = Arrays.binarySearch(splitCmds, "|");
		if (i > -1) {
			commands.add(splitCmds[i+1]);
			params.add(Arrays.copyOfRange(splitCmds, 1, i));
			params.add(Arrays.copyOfRange(splitCmds, i + 2, splitCmds.length));
		} else {
			params.add(Arrays.copyOfRange(splitCmds, 1, splitCmds.length));
		}
		handleCommands(commands, params);
	}


	private static void handleCommands(ArrayList<String> cmds, ArrayList<String[]> params) {
		for (int i = 0; i < cmds.size(); i++) {
			System.out.println("Executing command: " + cmds.get(i));
			System.out.println("params: ");
			printList(params.get(i));
		}
	}

	/**
	 * splits a command into an array of words
	 * also handles quotation marks
	 * @param command the line to be split
	 * @return an array of the words in the command, strings within quotes are one index of the array
	 */
	private static String[] splitCommand(String command) {
		java.util.ArrayList<String> matchList = new java.util.ArrayList<>();
		Pattern regex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
		Matcher regexMatcher = regex.matcher(command);
		while (regexMatcher.find()) {
			if (regexMatcher.group(1) != null) {
				// Add double-quoted string without the quotes
				matchList.add(regexMatcher.group(1));
			} else if (regexMatcher.group(2) != null) {
				// Add single-quoted string without the quotes
				matchList.add(regexMatcher.group(2));
			} else {
				// Add unquoted word
				matchList.add(regexMatcher.group());
			}
		}
		return matchList.toArray(new String[matchList.size()]);
	}

	private static void printList(List<String> list) {
		for (String l : list) {
			System.out.printf("[%s] ", l);
		}
		System.out.println();
	}

	private static void printList(String[] list) {
		for (String l : list) {
			System.out.printf("[%s] ", l);
		}
		System.out.println();
	}
}