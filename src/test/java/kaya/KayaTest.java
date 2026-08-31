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
    public void getResponse_invalidCommand_returnsUserFriendlyError() {
        Kaya kaya = new Kaya(temporaryDirectory.resolve("data").resolve("kaya.txt"));

        String response = kaya.getResponse("nonsense");

        assertTrue(response.startsWith("OOPS!!! I don't recognise that command."));
    }
}
