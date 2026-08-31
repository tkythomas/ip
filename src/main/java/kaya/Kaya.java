package kaya;

import java.io.IOException;
import java.nio.file.Path;

import kaya.command.CommandType;
import kaya.exception.KayaException;
import kaya.parser.Parser;
import kaya.storage.Storage;
import kaya.task.Task;
import kaya.task.TaskList;
import kaya.ui.Ui;

/**
 * Coordinates Kaya's user interface, task list, parser, and storage.
 */
public class Kaya {
    private static final String SYSTEM_NAME = "Kaya";
    private static final Path DATA_FILE = Path.of("data", "kaya.txt");

    private final Parser parser;
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;

    /**
     * Creates Kaya and loads tasks from the given data file.
     *
     * @param filePath the path of the task data file
     */
    public Kaya(Path filePath) {
        parser = new Parser();
        storage = new Storage(filePath);
        ui = new Ui();
        tasks = loadTasks();
    }

    /**
     * Runs the command loop until the user exits or input ends.
     */
    public void run() {
        ui.showGreeting(SYSTEM_NAME);
        while (ui.hasNextCommand()) {
            String input = ui.readCommand();
            ui.showLine();
            ui.showMessage(getResponse(input));
            ui.showLine();
            if (input.equals("bye")) {
                break;
            }
        }
        ui.close();
    }

    /**
     * Generates Kaya's response to one user command.
     *
     * <p>This method is shared by the console and JavaFX interfaces.</p>
     *
     * @param input the full command entered by the user
     * @return Kaya's response, including a user-friendly error for invalid input
     */
    public String getResponse(String input) {
        try {
            return processCommand(input.trim());
        } catch (KayaException exception) {
            return "OOPS!!! " + exception.getMessage();
        } catch (IOException exception) {
            return "OOPS!!! I couldn't save your tasks: " + exception.getMessage();
        }
    }

    /**
     * Executes one command and returns the resulting response.
     *
     * @param input the trimmed command entered by the user
     * @return Kaya's response to the command
     * @throws KayaException if the command is invalid
     * @throws IOException if updated tasks cannot be saved
     */
    private String processCommand(String input) throws KayaException, IOException {
        CommandType commandType = parser.parseCommandType(input);
        boolean tasksChanged = false;
        String response;

        switch (commandType) {
            case BYE -> {
                requireExactCommand(input, "bye");
                response = "Bye. Hope to see you again soon!";
            }
            case LIST -> {
                requireExactCommand(input, "list");
                response = formatTasks("Here are the tasks in your list:", tasks.asList());
            }
            case MARK -> {
                int index = parser.parseTaskIndex(input, "mark", tasks.size());
                Task task = tasks.get(index);
                task.markAsDone();
                response = "Nice! I've marked this task as done:\n  " + task;
                tasksChanged = true;
            }
            case UNMARK -> {
                int index = parser.parseTaskIndex(input, "unmark", tasks.size());
                Task task = tasks.get(index);
                task.markAsNotDone();
                response = "OK, I've marked this task as not done yet:\n  " + task;
                tasksChanged = true;
            }
            case DELETE -> {
                int index = parser.parseTaskIndex(input, "delete", tasks.size());
                Task removedTask = tasks.delete(index);
                response = "Noted. I've removed this task:\n  " + removedTask
                        + "\nNow you have " + tasks.size() + " tasks in the list.";
                tasksChanged = true;
            }
            case FIND -> {
                String keyword = parser.parseFindKeyword(input);
                response = formatTasks("Here are the matching tasks in your list:", tasks.find(keyword));
            }
            case TODO -> {
                response = addTask(parser.parseTodo(input));
                tasksChanged = true;
            }
            case DEADLINE -> {
                response = addTask(parser.parseDeadline(input));
                tasksChanged = true;
            }
            case EVENT -> {
                response = addTask(parser.parseEvent(input));
                tasksChanged = true;
            }
            case UNKNOWN -> throw new KayaException("I don't recognise that command. "
                    + "Try todo, deadline, event, list, find, mark, unmark, delete, or bye.");
            default -> throw new AssertionError("Unexpected command type: " + commandType);
        }

        if (tasksChanged) {
            storage.saveTasks(tasks.asList());
        }
        return response;
    }

    /**
     * Adds a task and displays confirmation.
     *
     * @param task the task to add
     */
    private String addTask(Task task) {
        tasks.add(task);
        return "Got it. I've added this task:\n  " + task
                + "\nNow you have " + tasks.size() + " tasks in the list.";
    }

    /**
     * Formats tasks as a heading followed by a one-based numbered list.
     *
     * @param heading the text displayed before the tasks
     * @param matchingTasks the tasks to display
     * @return the formatted task list
     */
    private String formatTasks(String heading, Iterable<Task> matchingTasks) {
        StringBuilder response = new StringBuilder(heading);
        int index = 1;
        for (Task task : matchingTasks) {
            response.append('\n').append(index).append('.').append(task);
            index++;
        }
        return response.toString();
    }

    /**
     * Loads saved tasks or starts with an empty list when loading fails.
     *
     * @return a task list containing any successfully loaded tasks
     */
    private TaskList loadTasks() {
        try {
            return new TaskList(storage.loadTasks());
        } catch (IOException exception) {
            ui.showError("I couldn't load your saved tasks. I'll start with an empty list.");
            return new TaskList();
        }
    }

    /**
     * Rejects extra details for a command that takes no arguments.
     *
     * @param input the full command entered by the user
     * @param command the expected command word
     * @throws KayaException if the input contains extra details
     */
    private void requireExactCommand(String input, String command) throws KayaException {
        if (!input.equals(command)) {
            throw new KayaException("The " + command + " command does not take any extra details.");
        }
    }

    /**
     * Starts Kaya using its default relative data-file path.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        new Kaya(DATA_FILE).run();
    }
}
