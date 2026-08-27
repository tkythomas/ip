package kaya.ui;

import java.util.List;
import java.util.Scanner;

import kaya.task.Task;

/** Handles all console input and output for Kaya. */
public class Ui {
    private static final String DIVIDER =
            "____________________________________________________________";
    private final Scanner scanner = new Scanner(System.in);

    /** Returns whether another command can be read. */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /** Reads and trims the next command. */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /** Displays Kaya's startup greeting. */
    public void showGreeting(String name) {
        showLine();
        showBanner();
        showMessage("Hello! I'm " + name + ".");
        showMessage("What can I do for you?");
        showLine();
    }

    /** Displays a message to the user. */
    public void showMessage(String message) {
        System.out.println(message);
    }

    /** Displays an error in Kaya's standard format. */
    public void showError(String message) {
        showMessage("OOPS!!! " + message);
    }

    /** Displays every task with a one-based number. */
    public void showTasks(List<Task> tasks) {
        showMessage("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            showMessage((i + 1) + "." + tasks.get(i));
        }
    }

    /** Displays the divider used between commands. */
    public void showLine() {
        showMessage(DIVIDER);
    }

    /** Displays Kaya's banner. */
    private void showBanner() {
        String banner = "  _  __            _  _          \n"
                        + " | |/ /   __ _    | || |  __ _   \n"
                        + " | ' <   / _` |    \\_, | / _` |  \n"
                        + " |_|\\_\\  \\__,_|   _|__/  \\__,_|  \n"
                        + "_|\"\"\"\"\"|_|\"\"\"\"\"|_| \"\"\"\"|_|\"\"\"\"\"| \n"
                        + "\"`-0-0-'\"`-0-0-'\"`-0-0-'\"`-0-0-'";
        showMessage(banner);
    }

    /** Closes the console input resource. */
    public void close() {
        scanner.close();
    }
}
