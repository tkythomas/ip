package kaya.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import kaya.exception.KayaException;
import kaya.task.Deadline;
import kaya.task.Event;

/** Tests command parsing and validation performed by {@link Parser}. */
public class ParserTest {
    private final Parser parser = new Parser();

    @Test
    public void parseDeadline_validInput_returnsDeadline() throws KayaException {
        Deadline deadline = parser.parseDeadline("deadline return book /by 2026-09-01");

        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDate.of(2026, 9, 1), deadline.getBy());
    }

    @Test
    public void parseFindKeyword_validInput_returnsKeyword() throws KayaException {
        assertEquals("project meeting", parser.parseFindKeyword("find project meeting"));
    }

    @Test
    public void parseFindKeyword_missingKeyword_throwsKayaException() {
        assertThrows(KayaException.class, () -> parser.parseFindKeyword("find"));
        assertThrows(KayaException.class, () -> parser.parseFindKeyword("find   "));
    }

    @Test
    public void parseDeadline_invalidDate_throwsKayaException() {
        assertThrows(KayaException.class,
                () -> parser.parseDeadline("deadline return book /by 01-09-2026"));
        assertThrows(KayaException.class,
                () -> parser.parseDeadline("deadline return book /by 2026-02-30"));
    }

    @Test
    public void parseDeadline_missingDescriptionOrSeparator_throwsKayaException() {
        assertThrows(KayaException.class,
                () -> parser.parseDeadline("deadline /by 2026-09-01"));
        assertThrows(KayaException.class,
                () -> parser.parseDeadline("deadline return book 2026-09-01"));
    }

    @Test
    public void parseEvent_validInput_returnsEvent() throws KayaException {
        Event event = parser.parseEvent(
                "event project meeting /from 2026-09-02 /to 2026-09-03");

        assertEquals("project meeting", event.getDescription());
        assertEquals(LocalDate.of(2026, 9, 2), event.getFrom());
        assertEquals(LocalDate.of(2026, 9, 3), event.getTo());
    }

    @Test
    public void parseEvent_endBeforeStart_throwsKayaException() {
        assertThrows(KayaException.class, () -> parser.parseEvent(
                "event project meeting /from 2026-09-03 /to 2026-09-02"));
    }

    @Test
    public void parseTaskIndex_validNumber_returnsZeroBasedIndex() throws KayaException {
        assertEquals(0, parser.parseTaskIndex("mark 1", "mark", 3));
        assertEquals(2, parser.parseTaskIndex("delete 3", "delete", 3));
    }

    @Test
    public void parseTaskIndex_invalidNumber_throwsKayaException() {
        assertThrows(KayaException.class,
                () -> parser.parseTaskIndex("mark", "mark", 3));
        assertThrows(KayaException.class,
                () -> parser.parseTaskIndex("mark two", "mark", 3));
        assertThrows(KayaException.class,
                () -> parser.parseTaskIndex("mark 0", "mark", 3));
        assertThrows(KayaException.class,
                () -> parser.parseTaskIndex("mark 4", "mark", 3));
    }
}
