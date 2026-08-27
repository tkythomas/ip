package kaya.command;

/**
 * Represents the commands understood by Kaya.
 */
public enum CommandType {
    /** Ends the chatbot session. */
    BYE("bye"),

    /** Displays all stored tasks. */
    LIST("list"),

    /** Marks a task as completed. */
    MARK("mark"),

    /** Marks a task as not completed. */
    UNMARK("unmark"),

    /** Removes a task from the list. */
    DELETE("delete"),

    /** Adds a task without a date. */
    TODO("todo"),

    /** Adds a task with a due date. */
    DEADLINE("deadline"),

    /** Adds a task with starting and ending dates. */
    EVENT("event"),

    /** Represents input that does not match a supported command. */
    UNKNOWN("");

    private final String commandWord;

    CommandType(String commandWord) {
        this.commandWord = commandWord;
    }

    /**
     * Identifies a command from the first word of the user's input.
     *
     * @param input the complete input entered by the user
     * @return the matching command type, or {@link #UNKNOWN} if there is no match
     */
    public static CommandType fromInput(String input) {
        int firstSpace = input.indexOf(' ');
        String firstWord = firstSpace < 0 ? input : input.substring(0, firstSpace);

        for (CommandType type : values()) {
            if (type != UNKNOWN && type.commandWord.equals(firstWord)) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
