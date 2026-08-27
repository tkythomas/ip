package kaya.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import kaya.task.Deadline;
import kaya.task.Event;
import kaya.task.Task;
import kaya.task.Todo;

/** Tests saving and loading tasks without touching Kaya's real data file. */
public class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void saveAndLoadTasks_multipleTaskTypes_restoresAllFields() throws IOException {
        Path file = temporaryDirectory.resolve("nested").resolve("tasks.txt");
        Storage storage = new Storage(file);
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("return book", LocalDate.of(2026, 9, 1));
        Event event = new Event("project meeting", LocalDate.of(2026, 9, 2),
                LocalDate.of(2026, 9, 3));
        deadline.markAsDone();

        storage.saveTasks(List.of(todo, deadline, event));
        List<Task> loadedTasks = storage.loadTasks();

        assertEquals(3, loadedTasks.size());
        assertInstanceOf(Todo.class, loadedTasks.get(0));
        assertEquals("read book", loadedTasks.get(0).getDescription());
        assertFalse(loadedTasks.get(0).isDone());

        Deadline loadedDeadline = assertInstanceOf(Deadline.class, loadedTasks.get(1));
        assertEquals("return book", loadedDeadline.getDescription());
        assertEquals(LocalDate.of(2026, 9, 1), loadedDeadline.getBy());
        assertTrue(loadedDeadline.isDone());

        Event loadedEvent = assertInstanceOf(Event.class, loadedTasks.get(2));
        assertEquals("project meeting", loadedEvent.getDescription());
        assertEquals(LocalDate.of(2026, 9, 2), loadedEvent.getFrom());
        assertEquals(LocalDate.of(2026, 9, 3), loadedEvent.getTo());
    }

    @Test
    public void loadTasks_missingFile_returnsEmptyList() throws IOException {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt"));

        assertTrue(storage.loadTasks().isEmpty());
    }

    @Test
    public void loadTasks_corruptedRecord_skipsRecordAndLoadsValidTasks() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(file);
        storage.saveTasks(List.of(new Todo("valid task")));
        Files.writeString(file, "corrupted record" + System.lineSeparator(),
                StandardCharsets.UTF_8, StandardOpenOption.APPEND);

        List<Task> loadedTasks = storage.loadTasks();

        assertEquals(1, loadedTasks.size());
        assertEquals("valid task", loadedTasks.get(0).getDescription());
    }

    @Test
    public void saveTasks_emptyList_createsEmptyDataFile() throws IOException {
        Path file = temporaryDirectory.resolve("data").resolve("tasks.txt");
        Storage storage = new Storage(file);

        storage.saveTasks(List.of());

        assertTrue(Files.exists(file));
        assertTrue(storage.loadTasks().isEmpty());
    }
}
