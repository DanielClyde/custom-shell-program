import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        File curDir = new File(System.getProperty("user.dir"));
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

class CDCommand extends BuiltInCommand {
    @Override
    public void run(String[] params) {
        if (params.length > 0 && !params[0].equals(".")) {
            try {
                Path p = Paths.get(System.getProperty("user.dir")).resolve(Paths.get(params[0]));
                p.normalize();
                if (p.toFile().isDirectory() || p.toFile().isFile()) {
                    if (params[0].equals("..")) {
                        File parentFile = new File(System.getProperty("user.dir")).getParentFile();
                        System.setProperty("user.dir", parentFile.getAbsolutePath());
                    } else {
                        System.setProperty("user.dir", p.toAbsolutePath().toString());
                    }
                } else {
                    System.out.println(params[0] + " is not a directory");
                }
            } catch (Exception e) {
                System.out.println(params[0] + " not a directory");
            }
        } else if (!(params.length > 0)){
            System.setProperty("user.dir", System.getProperty("user.home"));
        }
    }
}

class MDirCommand extends BuiltInCommand {
    @Override
    public void run(String[] params) {
        if (params.length > 0) {
            Path p = Paths.get(System.getProperty("user.dir")).resolve(Paths.get(params[0]));
            File f = p.toFile();
            boolean b = f.mkdir();
            if (!b) {
                System.out.println("Couldn't create specified directory, already exists!");
            }
        } else {
            System.out.println("Error, no directory name specified");
        }
    }
}

class RDirCommand extends BuiltInCommand {
    @Override
    public void run(String[] params) {
        if (params.length > 0) {
            Path p = Paths.get(System.getProperty("user.dir")).resolve(Paths.get(params[0]));
            File f = p.toFile();
            deleteFile(f);
        } else {
            System.out.println("Error, no directory name specified");
        }
    }

    /**
     * recursively delete a file and it's contents
     * @param file the file to delete
     * @author Danny Clyde
     */
    public void deleteFile (File file) {
        File[] children = file.listFiles();
        if (children != null) {
            for (File f : children) {
                deleteFile(f);
            }
        }
        file.delete();
    }
}
