package kaya;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Checks command replies and their effects on saved tasks.
 */
public class KayaTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void getResponse_addAndListTask_returnsExpectedReplies() {
        Kaya kaya = new Kaya(temporaryDirectory.resolve("data").resolve("kaya.txt"));

        String addResponse = kaya.getResponse("todo read book");
        String listResponse = kaya.getResponse("list");

        assertEquals("Added to your plate:\n  [T][ ] read book\nYou have 1 task on your plate.", addResponse);
        assertEquals("Here's what's on your plate:\n1.[T][ ] read book", listResponse);
    }

    @Test
    public void getResponse_taskCommands_preserveRepliesAndSavedChanges() {
        Path dataFile = temporaryDirectory.resolve("kaya.txt");
        Kaya kaya = new Kaya(dataFile);
        kaya.getResponse("todo read book");

        assertEquals("Settled! You've finished:\n  [T][X] read book",
                kaya.getResponse("mark 1"));
        assertEquals("Here's what's on your plate:\n1.[T][X] read book",
                new Kaya(dataFile).getResponse("list"));
        assertEquals("No rush. I've marked this task as not done yet:\n  [T][ ] read book",
                kaya.getResponse("unmark 1"));
        assertEquals("Here's what's on your plate:\n1.[T][ ] read book",
                new Kaya(dataFile).getResponse("list"));
        assertEquals("Here's what I found on your plate:\n1.[T][ ] read book",
                kaya.getResponse("find BOOK"));
        assertEquals("No matching tasks on your plate. Try another keyword.", kaya.getResponse("find missing"));
        assertEquals("Taken off your plate:\n  [T][ ] read book\nYou have 0 tasks on your plate.",
                kaya.getResponse("delete 1"));
        assertEquals("Your task list is empty. Time for a kopi break?", new Kaya(dataFile).getResponse("list"));
    }

    @Test
    public void getResponse_invalidTaskArguments_returnsErrorsWithoutChangingTasks() {
        Kaya kaya = new Kaya(temporaryDirectory.resolve("kaya.txt"));
        kaya.getResponse("todo read book");

        for (String command : new String[] {"mark 2", "unmark 0", "delete abc", "find"}) {
            assertTrue(kaya.getResponse(command).startsWith("Hmm. "));
            assertEquals("Here's what's on your plate:\n1.[T][ ] read book", kaya.getResponse("list"));
        }
    }

    @Test
    public void getResponse_invalidCommand_returnsUserFriendlyError() {
        Kaya kaya = new Kaya(temporaryDirectory.resolve("data").resolve("kaya.txt"));

        String response = kaya.getResponse("nonsense");

        assertTrue(response.startsWith("Hmm. I don't recognise that command."));
        assertTrue(response.contains("Try todo, deadline, event, list, find, mark, unmark, delete, update, or bye."));
    }
}
