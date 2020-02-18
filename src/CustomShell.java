import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.*;

/**
 * This class accepts lines of input and attempts to run built-in or external commands from it.
 * @author Danny Clyde
 */
public class CustomShell {
    private HashMap<String, BuiltInCommand> commandMap = new HashMap<>();
    private Scanner input = new Scanner(System.in);
    private ArrayList<String> history = new ArrayList<>();
    private long time = 0;

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
        this.commandMap.put("list", new ListCommand());
        this.commandMap.put("cd", new CDCommand());
        this.commandMap.put("mdir", new MDirCommand());
        this.commandMap.put("rdir", new RDirCommand());
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
        this.handleBuiltInCommands(commands, params);
        this.handleExternalCommands(splitCmds);
    }

    private void handleExternalCommands(String[] cmd) {
        int index = Arrays.binarySearch(cmd, "|");
        if (index > -1) {
            this.handleExternalPipeCommands(
                Arrays.copyOfRange(cmd, 0, index),
                Arrays.copyOfRange(cmd, index + 1, cmd.length));
        } else {
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.directory(new File(System.getProperty("user.dir")));
            pb.redirectInput(ProcessBuilder.Redirect.INHERIT);
            pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            try {
                long start = System.currentTimeMillis();
                Process p = pb.start();
                p.waitFor();
                long end = System.currentTimeMillis();
                this.time += (end - start);
            } catch (Exception e) { }
        }
    }

    /**
     * This code is from Dean Mathias's PipeDemo.java
     * @param cmd1
     * @param cmd2
     */
    private void handleExternalPipeCommands(String[] cmd1, String[] cmd2) {
        printList(cmd1);
        printList(cmd2);
        ProcessBuilder pb1 = new ProcessBuilder(cmd1);
        ProcessBuilder pb2 = new ProcessBuilder(cmd2);
        pb1.redirectInput(ProcessBuilder.Redirect.INHERIT);
//        pb1.redirectOutput(ProcessBuilder.Redirect.PIPE);
//        pb2.redirectInput(ProcessBuilder.Redirect.PIPE);
        pb2.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        try {
            Process p1 = pb1.start();
            Process p2 = pb2.start();
            java.io.InputStream in = p1.getInputStream();
            java.io.OutputStream out = p2.getOutputStream();

            int c;
            while ((c = in.read()) != -1) {
                out.write(c);
            }
            out.flush();
            out.close();
            p1.waitFor();
            p2.waitFor();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void handleBuiltInCommands(ArrayList<String> cmds, ArrayList<String[]> params) {
        for (int i = 0; i < cmds.size(); i++) {
            if (this.commandMap.containsKey(cmds.get(i))) {
                this.runBuiltInCmd(cmds.get(i), params.get(i));
            } else if (cmds.get(i).startsWith("^")) {
                int index = Integer.parseInt(cmds.get(i).substring(1));
                String newCmd = this.history.get(index);
                this.handleLine(newCmd);
            }
        }
    }

    private void runBuiltInCmd(String cmd, String[] params) {
        if (cmd.equals("history")) {
            this.commandMap.get(cmd).run(this.history.toArray(new String[0]));
        } else if (cmd.equals("ptime")) {
            this.commandMap.get(cmd).run(new String[]{String.valueOf(this.time),});
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
