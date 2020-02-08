import java.util.Scanner;

public class Assign3 {
	static Scanner input = new Scanner(System.in);
	public static void main(String[] args) {
		String cmd = "";
		while(!cmd.equals("exit")) {
			System.out.println(cmd);
			cmd = getNextCommand();
		}
	}

	public static String getNextCommand() {
		System.out.print("[" + System.getProperty("user.dir") + "]: ");
		return input.nextLine();
	}
}