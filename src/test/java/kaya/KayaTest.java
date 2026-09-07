package kaya;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class KayaTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void getResponse_addAndListTask_returnsExpectedReplies() {
        Kaya kaya = new Kaya(temporaryDirectory.resolve("data").resolve("kaya.txt"));

        String addResponse = kaya.getResponse("todo read book");
        String listResponse = kaya.getResponse("list");

        assertTrue(addResponse.contains("Got it. I've added this task:"));
        assertTrue(addResponse.contains("[T][ ] read book"));
        assertEquals("Here are the tasks in your list:\n1.[T][ ] read book", listResponse);
    }

    @Test
    public void getResponse_taskCommands_preserveRepliesAndSavedChanges() {
        Path dataFile = temporaryDirectory.resolve("kaya.txt");
        Kaya kaya = new Kaya(dataFile);
        kaya.getResponse("todo read book");

        assertEquals("Nice! I've marked this task as done:\n  [T][X] read book",
                kaya.getResponse("mark 1"));
        assertEquals("Here are the tasks in your list:\n1.[T][X] read book",
                new Kaya(dataFile).getResponse("list"));
        assertEquals("OK, I've marked this task as not done yet:\n  [T][ ] read book",
                kaya.getResponse("unmark 1"));
        assertEquals("Here are the tasks in your list:\n1.[T][ ] read book",
                new Kaya(dataFile).getResponse("list"));
        assertEquals("Here are the matching tasks in your list:\n1.[T][ ] read book",
                kaya.getResponse("find BOOK"));
        assertEquals("Noted. I've removed this task:\n  [T][ ] read book\nNow you have 0 tasks in the list.",
                kaya.getResponse("delete 1"));
        assertEquals("Here are the tasks in your list:", new Kaya(dataFile).getResponse("list"));
    }

    @Test
    public void getResponse_invalidTaskArguments_returnsErrorsWithoutChangingTasks() {
        Kaya kaya = new Kaya(temporaryDirectory.resolve("kaya.txt"));
        kaya.getResponse("todo read book");

        for (String command : new String[] {"mark 2", "unmark 0", "delete abc", "find"}) {
            assertTrue(kaya.getResponse(command).startsWith("OOPS!!! "));
            assertEquals("Here are the tasks in your list:\n1.[T][ ] read book", kaya.getResponse("list"));
        }
    }

    @Test
    public void getResponse_invalidCommand_returnsUserFriendlyError() {
        Kaya kaya = new Kaya(temporaryDirectory.resolve("data").resolve("kaya.txt"));

        String response = kaya.getResponse("nonsense");

        assertTrue(response.startsWith("OOPS!!! I don't recognise that command."));
    }
}
