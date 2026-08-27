package kaya.command;

/**
 * Represents the commands understood by Kaya.
 */
public enum CommandType {
    BYE("bye"),
    LIST("list"),
    MARK("mark"),
    UNMARK("unmark"),
    DELETE("delete"),
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event"),
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
