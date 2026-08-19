import java.util.Scanner;

/**
 * Runs the Kaya chatbot and stores tasks entered by the user.
 */
public class Kaya {
    private static final int MAX_TASKS = 100;
    private static final String SYSTEM_NAME = "Kaya";

    public static void main(String[] args) {
        greet(SYSTEM_NAME);

        Scanner scanner = new Scanner(System.in);
        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = 0;

        while (true) {
            String input = scanner.nextLine();
            printLine();

            if (input.equals("bye")) {
                exit();
                break;
            }

            if (input.equals("list")) {
                printTasks(tasks, taskCount);
            } else if (input.startsWith("mark ")) {
                int taskIndex = Integer.parseInt(input.substring(5)) - 1;
                tasks[taskIndex].markAsDone();
                System.out.println("Nice! I've marked this task as done:");
                System.out.println("  " + tasks[taskIndex]);
            } else if (input.startsWith("unmark ")) {
                int taskIndex = Integer.parseInt(input.substring(7)) - 1;
                tasks[taskIndex].markAsNotDone();
                System.out.println("OK, I've marked this task as not done yet:");
                System.out.println("  " + tasks[taskIndex]);
            } else {
                tasks[taskCount] = new Task(input);
                taskCount++;
                System.out.println("added: " + input);
            }

            printLine();
        }

        scanner.close();
    }

    /**
     * Prints all stored tasks in the order they were added.
     *
     * @param tasks the array containing the stored tasks
     * @param taskCount the number of tasks currently stored
     */
    public static void printTasks(Task[] tasks, int taskCount) {
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < taskCount; i++) {
            System.out.println((i + 1) + "." + tasks[i]);
        }
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
