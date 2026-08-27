import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/** Interprets user input and converts it into task data Kaya can use. */
public class Parser {
    /** Identifies the command at the start of the input. */
    public CommandType parseCommandType(String input) {
        return CommandType.fromInput(input);
    }

    /** Parses a {@code todo DESCRIPTION} command. */
    public Todo parseTodo(String input) throws KayaException {
        String description = input.substring("todo".length()).trim();
        if (description.isEmpty()) {
            throw new KayaException("A todo needs a description.");
        }
        return new Todo(description);
    }

    /** Parses a {@code deadline DESCRIPTION /by yyyy-MM-dd} command. */
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

    /** Parses an {@code event DESCRIPTION /from yyyy-MM-dd /to yyyy-MM-dd} command. */
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

    /** Parses and validates a one-based task number, returning a zero-based index. */
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

    /** Parses a date in ISO {@code yyyy-MM-dd} format. */
    private LocalDate parseDate(String dateText) throws KayaException {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException exception) {
            throw new KayaException("Use dates in yyyy-MM-dd format, for example 2019-10-15.");
        }
    }
}
