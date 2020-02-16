import java.io.File;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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

/**
 * Got some guidance and ideas on this method from stack overflow
 */
class ListCommand extends BuiltInCommand {
    @Override
    public void run(String[] params) {
        File curDir = new File(".");
        File[] fileList = curDir.listFiles();
        for (File f : fileList) {
            StringBuilder sb = new StringBuilder();
            if (f.isDirectory()) {
                sb.append("d");
                sb.append(f.canRead() ? "r" : "-");
                sb.append(f.canWrite() ? "w" : "-");
                sb.append(f.canExecute() ? "x" : "-");
                sb.append("\t");
                sb.append(String.format("%1$10d", f.length()));
                sb.append("\t");
                Date d = new Date(f.lastModified());
                DateFormat dateFormat = new SimpleDateFormat("MMM d, y HH:mm");
                sb.append(dateFormat.format(d));
                sb.append("\t");
                sb.append(f.getName());
            } else if (f.isFile()) {
                sb.append("-");
                sb.append(f.canRead() ? "r" : "-");
                sb.append(f.canWrite() ? "w" : "-");
                sb.append(f.canExecute() ? "x" : "-");
                sb.append("\t");
                sb.append(String.format("%1$10d", f.length()));
                sb.append("\t");
                Date d = new Date(f.lastModified());
                DateFormat dateFormat = new SimpleDateFormat("MMM d, y HH:mm");
                sb.append(dateFormat.format(d));
                sb.append("\t");
                sb.append(f.getName());
            }
            System.out.println(sb.toString());
        }

    }
}
