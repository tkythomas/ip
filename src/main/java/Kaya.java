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
            } else if (input.startsWith("todo ")) {
                Task task = new Todo(input.substring(5));
                taskCount = addTask(tasks, taskCount, task);
            } else if (input.startsWith("deadline ")) {
                Task task = parseDeadline(input);
                taskCount = addTask(tasks, taskCount, task);
            } else if (input.startsWith("event ")) {
                Task task = parseEvent(input);
                taskCount = addTask(tasks, taskCount, task);
            } else {
                Task task = new Todo(input);
                taskCount = addTask(tasks, taskCount, task);
            }

            printLine();
        }

        scanner.close();
    }

    /**
     * Stores a task and prints a confirmation containing the updated task count.
     *
     * @param tasks the array in which tasks are stored
     * @param taskCount the number of tasks stored before this addition
     * @param task the task to add
     * @return the updated number of stored tasks
     */
    public static int addTask(Task[] tasks, int taskCount, Task task) {
        tasks[taskCount] = task;
        int updatedTaskCount = taskCount + 1;
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + updatedTaskCount + " tasks in the list.");
        return updatedTaskCount;
    }

    /**
     * Converts a deadline command into a deadline task.
     *
     * @param input a command in the format {@code deadline DESCRIPTION /by DATE_OR_TIME}
     * @return the parsed deadline
     */
    public static Deadline parseDeadline(String input) {
        String details = input.substring("deadline ".length());
        int bySeparator = details.indexOf(" /by ");
        String description = details.substring(0, bySeparator);
        String by = details.substring(bySeparator + " /by ".length());
        return new Deadline(description, by);
    }

    /**
     * Converts an event command into an event task.
     *
     * @param input a command in the format
     *              {@code event DESCRIPTION /from START /to END}
     * @return the parsed event
     */
    public static Event parseEvent(String input) {
        String details = input.substring("event ".length());
        int fromSeparator = details.indexOf(" /from ");
        int toSeparator = details.indexOf(" /to ", fromSeparator + " /from ".length());
        String description = details.substring(0, fromSeparator);
        String from = details.substring(fromSeparator + " /from ".length(), toSeparator);
        String to = details.substring(toSeparator + " /to ".length());
        return new Event(description, from, to);
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
