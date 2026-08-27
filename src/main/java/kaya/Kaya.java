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

/** Coordinates Kaya's user interface, task list, parser, and storage. */
public class Kaya {
    private static final String SYSTEM_NAME = "Kaya";
    private static final Path DATA_FILE = Path.of("data", "kaya.txt");

    private final Parser parser;
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;

    /** Creates Kaya and loads tasks from the given data file. */
    public Kaya(Path filePath) {
        parser = new Parser();
        storage = new Storage(filePath);
        ui = new Ui();
        tasks = loadTasks();
    }

    /** Runs the command loop until the user exits or input ends. */
    public void run() {
        ui.showGreeting(SYSTEM_NAME);
        while (ui.hasNextCommand()) {
            String input = ui.readCommand();
            ui.showLine();
            try {
                if (!processCommand(input)) {
                    break;
                }
            } catch (KayaException exception) {
                ui.showError(exception.getMessage());
            } catch (IOException exception) {
                ui.showError("I couldn't save your tasks: " + exception.getMessage());
            }
            ui.showLine();
        }
        ui.close();
    }

    /** Executes one command and reports whether the command loop should continue. */
    private boolean processCommand(String input) throws KayaException, IOException {
        CommandType commandType = parser.parseCommandType(input);
        boolean tasksChanged = false;

        switch (commandType) {
            case BYE -> {
                requireExactCommand(input, "bye");
                ui.showMessage("Bye. Hope to see you again soon!");
                ui.showLine();
                return false;
            }
            case LIST -> {
                requireExactCommand(input, "list");
                ui.showTasks(tasks.asList());
            }
            case MARK -> {
                int index = parser.parseTaskIndex(input, "mark", tasks.size());
                Task task = tasks.get(index);
                task.markAsDone();
                ui.showMessage("Nice! I've marked this task as done:");
                ui.showMessage("  " + task);
                tasksChanged = true;
            }
            case UNMARK -> {
                int index = parser.parseTaskIndex(input, "unmark", tasks.size());
                Task task = tasks.get(index);
                task.markAsNotDone();
                ui.showMessage("OK, I've marked this task as not done yet:");
                ui.showMessage("  " + task);
                tasksChanged = true;
            }
            case DELETE -> {
                int index = parser.parseTaskIndex(input, "delete", tasks.size());
                Task removedTask = tasks.delete(index);
                ui.showMessage("Noted. I've removed this task:");
                ui.showMessage("  " + removedTask);
                ui.showMessage("Now you have " + tasks.size() + " tasks in the list.");
                tasksChanged = true;
            }
            case TODO -> {
                addTask(parser.parseTodo(input));
                tasksChanged = true;
            }
            case DEADLINE -> {
                addTask(parser.parseDeadline(input));
                tasksChanged = true;
            }
            case EVENT -> {
                addTask(parser.parseEvent(input));
                tasksChanged = true;
            }
            case UNKNOWN -> throw new KayaException("I don't recognise that command. "
                    + "Try todo, deadline, event, list, mark, unmark, delete, or bye.");
        }

        if (tasksChanged) {
            storage.saveTasks(tasks.asList());
        }
        return true;
    }

    /** Adds a task and displays confirmation. */
    private void addTask(Task task) {
        tasks.add(task);
        ui.showMessage("Got it. I've added this task:");
        ui.showMessage("  " + task);
        ui.showMessage("Now you have " + tasks.size() + " tasks in the list.");
    }

    /** Loads saved tasks or starts with an empty list when loading fails. */
    private TaskList loadTasks() {
        try {
            return new TaskList(storage.loadTasks());
        } catch (IOException exception) {
            ui.showError("I couldn't load your saved tasks. I'll start with an empty list.");
            return new TaskList();
        }
    }

    /** Rejects extra details for a command that takes no arguments. */
    private void requireExactCommand(String input, String command) throws KayaException {
        if (!input.equals(command)) {
            throw new KayaException("The " + command + " command does not take any extra details.");
        }
    }

    /** Starts Kaya using its default relative data-file path. */
    public static void main(String[] args) {
        new Kaya(DATA_FILE).run();
    }
}
