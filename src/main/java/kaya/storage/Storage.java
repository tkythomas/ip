package kaya.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import kaya.task.Deadline;
import kaya.task.Event;
import kaya.task.Task;
import kaya.task.Todo;

/**
 * Saves and loads Kaya's tasks using a text file on disk.
 *
 * <p>Text fields are Base64 encoded so descriptions containing separators or
 * line breaks can be restored without ambiguity.</p>
 */
public class Storage {
    private static final String FIELD_SEPARATOR = " | ";
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder();
    private static final Base64.Decoder DECODER = Base64.getUrlDecoder();

    private final Path filePath;

    /**
     * Creates storage that reads from and writes to the given path.
     *
     * @param filePath the data file path
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Writes every task to disk, creating the parent directory when necessary.
     *
     * @param tasks the tasks to save
     * @throws IOException if the directory or file cannot be written
     */
    public void saveTasks(List<Task> tasks) throws IOException {
        Path parent = filePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        List<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            lines.add(serialize(task));
        }
        Files.write(filePath, lines, StandardCharsets.UTF_8);
    }

    /**
     * Loads valid tasks from disk. A missing file represents an empty task list,
     * while malformed lines are ignored so one damaged record cannot stop Kaya.
     *
     * @return the tasks found in the data file
     * @throws IOException if an existing data file cannot be read
     */
    public List<Task> loadTasks() throws IOException {
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return tasks;
        }

        for (String line : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
            try {
                tasks.add(deserialize(line));
            } catch (IllegalArgumentException | DateTimeException exception) {
                // Skip corrupted records but continue loading the remaining tasks.
            }
        }
        return tasks;
    }

    /**
     * Converts a task into one line of the storage format.
     */
    private String serialize(Task task) {
        String status = task.isDone() ? "1" : "0";
        if (task instanceof Deadline deadline) {
            return String.join(FIELD_SEPARATOR, "D", status,
                    encode(task.getDescription()), encode(deadline.getBy().toString()));
        }
        if (task instanceof Event event) {
            return String.join(FIELD_SEPARATOR, "E", status,
                    encode(task.getDescription()), encode(event.getFrom().toString()),
                    encode(event.getTo().toString()));
        }
        return String.join(FIELD_SEPARATOR, "T", status, encode(task.getDescription()));
    }

    /**
     * Converts one valid storage line back into a task.
     */
    private Task deserialize(String line) {
        String[] fields = line.split(" \\| ", -1);
        if (fields.length < 3 || !(fields[1].equals("0") || fields[1].equals("1"))) {
            throw new IllegalArgumentException("Invalid task record");
        }

        Task task = switch (fields[0]) {
            case "T" -> {
                requireLength(fields, 3);
                yield new Todo(decode(fields[2]));
            }
            case "D" -> {
                requireLength(fields, 4);
                yield new Deadline(decode(fields[2]), LocalDate.parse(decode(fields[3])));
            }
            case "E" -> {
                requireLength(fields, 5);
                yield new Event(decode(fields[2]), LocalDate.parse(decode(fields[3])),
                        LocalDate.parse(decode(fields[4])));
            }
            default -> throw new IllegalArgumentException("Unknown task type");
        };

        if (fields[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Checks a record's field count before returning its parsed task.
     */
    private void requireLength(String[] fields, int expectedLength) {
        if (fields.length != expectedLength) {
            throw new IllegalArgumentException("Incorrect number of fields");
        }
    }

    /**
     * Encodes text as a single safe field in the data file.
     */
    private String encode(String value) {
        return ENCODER.encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Decodes a text field from the data file.
     */
    private String decode(String value) {
        return new String(DECODER.decode(value), StandardCharsets.UTF_8);
    }
}
