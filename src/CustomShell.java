import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomShell {
    private HashMap<String, BuiltInCommand> commandMap = new HashMap<>();
    private Scanner input = new Scanner(System.in);
    private ArrayList<String> history = new ArrayList<>();
    private String ptime = "the ptime";

    public CustomShell() {
        this.initializeCommandMap();
    }
    public void run()  {
        String cmd = "";
        while(!cmd.equals("exit")) {
            cmd = getNextLine();
            this.handleLine(cmd);
        }
        System.out.println("terminating custom shell...");
    }

    private void initializeCommandMap() {
        this.commandMap.put("ptime", new PTimeCommand());
        this.commandMap.put("history", new HistoryCommand());
        this.commandMap.put("list", new HistoryCommand());
        this.commandMap.put("cd", new HistoryCommand());
        this.commandMap.put("mdir", new HistoryCommand());
        this.commandMap.put("rdir", new HistoryCommand());
    }

    /**
     * give user prompt (current dir) and wait for their next input
     * @return the line of user input
     */
    private String getNextLine() {
        System.out.print("[" + System.getProperty("user.dir") + "]: ");
        return this.input.nextLine();
    }

    private void handleLine(String cmd) {
        String[] splitCmds = splitCommand(cmd);
        if (splitCmds.length < 1) return;
        if (splitCmds[0].equals("exit")) return;
        this.history.add(cmd);
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


    private void handleCommands(ArrayList<String> cmds, ArrayList<String[]> params) {
        for (int i = 0; i < cmds.size(); i++) {
            if (this.commandMap.containsKey(cmds.get(i))) {
                System.out.println("running " + cmds.get(i));
                this.runBuiltInCmd(cmds.get(i), params.get(i));
            }
        }
    }

    private void runBuiltInCmd(String cmd, String[] params) {
        if (cmd.equals("history")) {
            this.commandMap.get(cmd).run(this.history.toArray(new String[0]));
        } else if (cmd.equals("ptime")) {
            this.commandMap.get(cmd).run(new String[]{this.ptime,});
        } else {
            this.commandMap.get(cmd).run(params);
        }
    }

    /**
     * splits a command into an array of words
     * also handles quotation marks
     * @param command the line to be split
     * @return an array of the words in the command, strings within quotes are one index of the array
     */
    private String[] splitCommand(String command) {
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

    private static void printList(String[] list) {
        for (String l : list) {
            System.out.printf("[%s] ", l);
        }
        System.out.println();
    }
}
