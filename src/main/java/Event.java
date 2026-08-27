/**
 * Represents a task that occurs between specified start and end times.
 */
public class Event extends Task {
    private final String from;
    private final String to;

    /**
     * Creates an event with the given description, start, and end.
     *
     * @param description the description of the event
     * @param from the event's starting date or time
     * @param to the event's ending date or time
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event's starting date or time for saving to disk.
     *
     * @return the event's starting date or time
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the event's ending date or time for saving to disk.
     *
     * @return the event's ending date or time
     */
    public String getTo() {
        return to;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
