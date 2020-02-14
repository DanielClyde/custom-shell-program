abstract class BuiltInCommand {
    public abstract void run(String[] params);
}

class HistoryCommand extends BuiltInCommand {
    @Override
    public void run(String[] params) {
        for (int i = 0; i < params.length; i++) {
            System.out.println(i + ":\t\t" + params[i]);
        }
    }
}

class PTimeCommand extends BuiltInCommand {
    @Override
    public void run(String[] params) {
        System.out.println(params[0]);
    }
}
