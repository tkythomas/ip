package kaya.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import kaya.command.CommandType;
import kaya.exception.KayaException;
import kaya.task.Deadline;
import kaya.task.Event;
import kaya.task.Todo;

/**
 * Interprets user input and converts it into commands and task data Kaya can use.
 */
public class Parser {
    /**
     * Creates a parser for interpreting Kaya commands.
     */
    public Parser() {
        // Parser has no state to initialize.
    }

    /**
     * Identifies the command at the start of the input.
     *
     * @param input the full command entered by the user
     * @return the matching command type, or {@link CommandType#UNKNOWN}
     */
    public CommandType parseCommandType(String input) {
        return CommandType.fromInput(input);
    }

    /**
     * Parses a {@code todo DESCRIPTION} command.
     *
     * @param input the full todo command
     * @return the parsed todo task
     * @throws KayaException if the description is missing
     */
    public Todo parseTodo(String input) throws KayaException {
        String description = input.substring("todo".length()).trim();
        if (description.isEmpty()) {
            throw new KayaException("A todo needs a description.");
        }
        return new Todo(description);
    }

    /**
     * Parses a {@code deadline DESCRIPTION /by yyyy-MM-dd} command.
     *
     * @param input the full deadline command
     * @return the parsed deadline task
     * @throws KayaException if the description, separator, or date is invalid
     */
    public Deadline parseDeadline(String input) throws KayaException {
        String details = input.substring("deadline".length()).trim();
        int separator = details.indexOf(" /by ");
        if (separator < 0) {
            throw new KayaException("Use deadlines like: deadline DESCRIPTION /by yyyy-MM-dd.");
        }
        String description = details.substring(0, separator).trim();
        String byText = details.substring(separator + " /by ".length()).trim();
        if (description.isEmpty()) {
            throw new KayaException("A deadline needs a description.");
        }
        if (byText.isEmpty()) {
            throw new KayaException("A deadline needs a date after /by.");
        }
        return new Deadline(description, parseDate(byText));
    }

    /**
     * Parses an {@code event DESCRIPTION /from yyyy-MM-dd /to yyyy-MM-dd} command.
     *
     * @param input the full event command
     * @return the parsed event task
     * @throws KayaException if required details are missing or invalid
     */
    public Event parseEvent(String input) throws KayaException {
        String details = input.substring("event".length()).trim();
        int fromSeparator = details.indexOf(" /from ");
        if (fromSeparator < 0) {
            throw new KayaException(
                    "Use events like: event DESCRIPTION /from yyyy-MM-dd /to yyyy-MM-dd.");
        }
        int toSeparator = details.indexOf(" /to ", fromSeparator + " /from ".length());
        if (toSeparator < 0) {
            throw new KayaException("An event needs an ending date after /to.");
        }

        String description = details.substring(0, fromSeparator).trim();
        String fromText = details.substring(fromSeparator + " /from ".length(), toSeparator).trim();
        String toText = details.substring(toSeparator + " /to ".length()).trim();
        if (description.isEmpty()) {
            throw new KayaException("An event needs a description.");
        }
        if (fromText.isEmpty()) {
            throw new KayaException("An event needs a starting date after /from.");
        }
        if (toText.isEmpty()) {
            throw new KayaException("An event needs an ending date after /to.");
        }

        LocalDate from = parseDate(fromText);
        LocalDate to = parseDate(toText);
        if (to.isBefore(from)) {
            throw new KayaException("An event's ending date cannot be before its starting date.");
        }
        return new Event(description, from, to);
    }

    /**
     * Parses and validates a one-based task number, returning a zero-based index.
     *
     * @param input the full task-index command
     * @param command the command word preceding the task number
     * @param taskCount the number of tasks that can be selected
     * @return the corresponding zero-based task index
     * @throws KayaException if the number is missing, invalid, or out of range
     */
    public int parseTaskIndex(String input, String command, int taskCount) throws KayaException {
        String taskNumber = input.substring(command.length()).trim();
        if (taskNumber.isEmpty()) {
            throw new KayaException("Tell me which task number to " + command + ".");
        }
        int index;
        try {
            index = Integer.parseInt(taskNumber) - 1;
        } catch (NumberFormatException exception) {
            throw new KayaException("The task number must be a whole number.");
        }
        if (index < 0 || index >= taskCount) {
            throw new KayaException("That task number is not in your list.");
        }
        return index;
    }

    /**
     * Parses a date in ISO {@code yyyy-MM-dd} format.
     *
     * @param dateText the date text to parse
     * @return the parsed date
     * @throws KayaException if the date is invalid or uses another format
     */
    private LocalDate parseDate(String dateText) throws KayaException {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException exception) {
            throw new KayaException("Use dates in yyyy-MM-dd format, for example 2019-10-15.");
        }
    }
}
