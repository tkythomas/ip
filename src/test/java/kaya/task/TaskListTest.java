package kaya.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests task-list searches. */
public class TaskListTest {
    @Test
    public void find_keywordInDescriptions_returnsMatchingTasksInOrder() {
        TaskList tasks = new TaskList(List.of(
                new Todo("read book"),
                new Todo("return book"),
                new Todo("buy groceries")));

        List<Task> matches = tasks.find("book");

        assertEquals(2, matches.size());
        assertEquals("read book", matches.get(0).getDescription());
        assertEquals("return book", matches.get(1).getDescription());
    }

    @Test
    public void find_keywordWithDifferentCase_returnsMatchingTask() {
        TaskList tasks = new TaskList(List.of(new Todo("Read Book")));

        List<Task> matches = tasks.find("book");

        assertEquals(1, matches.size());
        assertEquals("Read Book", matches.get(0).getDescription());
    }

    @Test
    public void find_keywordNotPresent_returnsEmptyList() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertTrue(tasks.find("meeting").isEmpty());
    }
}
