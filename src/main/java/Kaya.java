import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Runs the Kaya chatbot and stores tasks entered by the user.
 */
public class Kaya {
    private static final String SYSTEM_NAME = "Kaya";
    private static final Path DATA_FILE = Path.of("data", "kaya.txt");

    public static void main(String[] args) {
        greet(SYSTEM_NAME);

        Scanner scanner = new Scanner(System.in);
        Storage storage = new Storage(DATA_FILE);
        List<Task> tasks = loadTasks(storage);

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();
            printLine();

            try {
                CommandType commandType = CommandType.fromInput(input);
                boolean shouldContinue = processCommand(commandType, input, tasks);
                if (changesTaskList(commandType)) {
                    storage.saveTasks(tasks);
                }
                if (!shouldContinue) {
                    break;
                }
            } catch (KayaException exception) {
                System.out.println("OOPS!!! " + exception.getMessage());
            } catch (IOException exception) {
                System.out.println("OOPS!!! I couldn't save your tasks: "
                        + exception.getMessage());
            }

            printLine();
        }

        scanner.close();
    }

    /**
     * Loads saved tasks, or starts with an empty list if the data cannot be read.
     *
     * @param storage the storage used by Kaya
     * @return the loaded tasks, or an empty list when loading fails
     */
    private static List<Task> loadTasks(Storage storage) {
        try {
            return storage.loadTasks();
        } catch (IOException exception) {
            System.out.println("OOPS!!! I couldn't load your saved tasks. "
                    + "I'll start with an empty list.");
            return new ArrayList<>();
        }
    }

    /**
     * Returns whether a successfully processed command changes stored task data.
     *
     * @param commandType the processed command type
     * @return {@code true} if the updated tasks should be saved
     */
    private static boolean changesTaskList(CommandType commandType) {
        return switch (commandType) {
        case MARK, UNMARK, DELETE, TODO, DEADLINE, EVENT -> true;
        default -> false;
        };
    }

    /**
     * Executes one command.
     *
     * @param commandType the type of command to execute
     * @param input the command entered by the user
     * @param tasks the list in which tasks are stored
     * @return {@code false} if Kaya should exit, or {@code true} otherwise
     * @throws KayaException if the command is invalid
     */
    public static boolean processCommand(CommandType commandType, String input,
                                         List<Task> tasks) throws KayaException {
        switch (commandType) {
        case BYE -> {
            if (!input.equals("bye")) {
                throw new KayaException("The bye command does not take any extra details.");
            }
            exit();
            return false;
        }
        case LIST -> {
            if (!input.equals("list")) {
                throw new KayaException("The list command does not take any extra details.");
            }
            printTasks(tasks);
        }
        case MARK -> {
            int taskIndex = parseTaskIndex(input, "mark", tasks.size());
            Task task = tasks.get(taskIndex);
            task.markAsDone();
            System.out.println("Nice! I've marked this task as done:");
            System.out.println("  " + task);
        }
        case UNMARK -> {
            int taskIndex = parseTaskIndex(input, "unmark", tasks.size());
            Task task = tasks.get(taskIndex);
            task.markAsNotDone();
            System.out.println("OK, I've marked this task as not done yet:");
            System.out.println("  " + task);
        }
        case DELETE -> {
            int taskIndex = parseTaskIndex(input, "delete", tasks.size());
            Task removedTask = tasks.remove(taskIndex);
            System.out.println("Noted. I've removed this task:");
            System.out.println("  " + removedTask);
            System.out.println("Now you have " + tasks.size() + " tasks in the list.");
        }
        case TODO -> {
            String description = input.substring("todo".length()).trim();
            if (description.isEmpty()) {
                throw new KayaException("A todo needs a description.");
            }
            addTask(tasks, new Todo(description));
        }
        case DEADLINE -> addTask(tasks, parseDeadline(input));
        case EVENT -> addTask(tasks, parseEvent(input));
        case UNKNOWN ->
            throw new KayaException("I don't recognise that command. "
                    + "Try todo, deadline, event, list, mark, unmark, delete, or bye.");
        }
        return true;
    }

    /**
     * Stores a task and prints a confirmation containing the updated task count.
     *
     * @param tasks the list in which tasks are stored
     * @param task the task to add
     */
    public static void addTask(List<Task> tasks, Task task) {
        tasks.add(task);
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
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
     * Parses and validates the one-based task number in a task-index command.
     *
     * @param input the complete command
     * @param command the command word, such as {@code mark}, {@code unmark}, or {@code delete}
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
     * @param tasks the list containing the stored tasks
     */
    public static void printTasks(List<Task> tasks) {
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
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
