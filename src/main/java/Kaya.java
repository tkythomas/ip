import java.util.Scanner;

/**
 * Runs the Kaya chatbot and echoes commands entered by the user.
 */
public class Kaya {
    private static final String SYSTEM_NAME = "Kaya";

    public static void main(String[] args) {
        greet(SYSTEM_NAME);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            printLine();

            if (input.equals("bye")) {
                exit();
                break;
            }

            System.out.println(input);
            printLine();
        }

        scanner.close();
    }

    public static void greet(String name) {
        printLine();
        printBanner();
        System.out.println("Hello! I'm " + name + ".");
        System.out.println("What can I do for you?");
        printLine();
    }

    public static void printBanner() {
        String banner =   "  _  __            _  _          \n"
                        + " | |/ /   __ _    | || |  __ _   \n"
                        + " | ' <   / _` |    \\_, | / _` |  \n"
                        + " |_|\\_\\  \\__,_|   _|__/  \\__,_|  \n"
                        + "_|\"\"\"\"\"|_|\"\"\"\"\"|_| \"\"\"\"|_|\"\"\"\"\"| \n"
                        + "\"`-0-0-'\"`-0-0-'\"`-0-0-'\"`-0-0-'";
        System.out.println(banner);
    }

    public static void printLine() {
        System.out.println("____________________________________________________________");
    }

    public static void exit() {
        System.out.println("Bye. Hope to see you again soon!");
        printLine();
    }

}
