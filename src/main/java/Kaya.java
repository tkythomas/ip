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

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();
            printLine();

            if (input.equals("bye")) {
                exit();
                break;
            }

            try {
                taskCount = processCommand(input, tasks, taskCount);
            } catch (KayaException exception) {
                System.out.println("OOPS!!! " + exception.getMessage());
            }

            printLine();
        }

        scanner.close();
    }

    /**
     * Executes one non-exit command and returns the resulting task count.
     *
     * @param input the command entered by the user
     * @param tasks the array in which tasks are stored
     * @param taskCount the current number of stored tasks
     * @return the number of stored tasks after executing the command
     * @throws KayaException if the command is invalid
     */
    public static int processCommand(String input, Task[] tasks, int taskCount)
            throws KayaException {
        if (input.equals("list")) {
            printTasks(tasks, taskCount);
            return taskCount;
        } else if (input.equals("mark") || input.startsWith("mark ")) {
            int taskIndex = parseTaskIndex(input, "mark", taskCount);
            tasks[taskIndex].markAsDone();
            System.out.println("Nice! I've marked this task as done:");
            System.out.println("  " + tasks[taskIndex]);
            return taskCount;
        } else if (input.equals("unmark") || input.startsWith("unmark ")) {
            int taskIndex = parseTaskIndex(input, "unmark", taskCount);
            tasks[taskIndex].markAsNotDone();
            System.out.println("OK, I've marked this task as not done yet:");
            System.out.println("  " + tasks[taskIndex]);
            return taskCount;
        } else if (input.equals("todo") || input.startsWith("todo ")) {
            String description = input.substring("todo".length()).trim();
            if (description.isEmpty()) {
                throw new KayaException("A todo needs a description.");
            }
            return addTask(tasks, taskCount, new Todo(description));
        } else if (input.equals("deadline") || input.startsWith("deadline ")) {
            return addTask(tasks, taskCount, parseDeadline(input));
        } else if (input.equals("event") || input.startsWith("event ")) {
            return addTask(tasks, taskCount, parseEvent(input));
        }

        throw new KayaException("I don't recognise that command. "
                + "Try todo, deadline, event, list, mark, unmark, or bye.");
    }

    /**
     * Stores a task and prints a confirmation containing the updated task count.
     *
     * @param tasks the array in which tasks are stored
     * @param taskCount the number of tasks stored before this addition
     * @param task the task to add
     * @return the updated number of stored tasks
     */
    public static int addTask(Task[] tasks, int taskCount, Task task) throws KayaException {
        if (taskCount >= MAX_TASKS) {
            throw new KayaException("Your task list is full. Complete some tasks first.");
        }
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
     * @throws KayaException if the description, separator, or due date is missing
     */
    public static Deadline parseDeadline(String input) throws KayaException {
        String details = input.substring("deadline".length()).trim();
        int bySeparator = details.indexOf(" /by ");
        if (bySeparator < 0) {
            throw new KayaException("Use deadlines like: deadline DESCRIPTION /by DATE_OR_TIME.");
        }

        String description = details.substring(0, bySeparator).trim();
        String by = details.substring(bySeparator + " /by ".length()).trim();
        if (description.isEmpty()) {
            throw new KayaException("A deadline needs a description.");
        }
        if (by.isEmpty()) {
            throw new KayaException("A deadline needs a date or time after /by.");
        }
        return new Deadline(description, by);
    }

    /**
     * Converts an event command into an event task.
     *
     * @param input a command in the format
     *              {@code event DESCRIPTION /from START /to END}
     * @return the parsed event
     * @throws KayaException if required event information is missing
     */
    public static Event parseEvent(String input) throws KayaException {
        String details = input.substring("event".length()).trim();
        int fromSeparator = details.indexOf(" /from ");
        if (fromSeparator < 0) {
            throw new KayaException("Use events like: event DESCRIPTION /from START /to END.");
        }

        int toSeparator = details.indexOf(" /to ", fromSeparator + " /from ".length());
        if (toSeparator < 0) {
            throw new KayaException("An event needs an ending date or time after /to.");
        }

        String description = details.substring(0, fromSeparator).trim();
        String from = details.substring(fromSeparator + " /from ".length(), toSeparator).trim();
        String to = details.substring(toSeparator + " /to ".length()).trim();
        if (description.isEmpty()) {
            throw new KayaException("An event needs a description.");
        }
        if (from.isEmpty()) {
            throw new KayaException("An event needs a starting date or time after /from.");
        }
        if (to.isEmpty()) {
            throw new KayaException("An event needs an ending date or time after /to.");
        }
        return new Event(description, from, to);
    }

    /**
     * Parses and validates the one-based task number in a mark or unmark command.
     *
     * @param input the complete command
     * @param command the command word, either {@code mark} or {@code unmark}
     * @param taskCount the number of tasks that can be selected
     * @return the corresponding zero-based array index
     * @throws KayaException if the task number is missing, invalid, or out of range
     */
    public static int parseTaskIndex(String input, String command, int taskCount)
            throws KayaException {
        String taskNumber = input.substring(command.length()).trim();
        if (taskNumber.isEmpty()) {
            throw new KayaException("Tell me which task number to " + command + ".");
        }

        int taskIndex;
        try {
            taskIndex = Integer.parseInt(taskNumber) - 1;
        } catch (NumberFormatException exception) {
            throw new KayaException("The task number must be a whole number.");
        }

        if (taskIndex < 0 || taskIndex >= taskCount) {
            throw new KayaException("That task number is not in your list.");
        }
        return taskIndex;
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
