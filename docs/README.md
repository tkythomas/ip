# Kaya User Guide

// Update the title above to match the actual product name

// Product screenshot goes here

Kaya is your kopitiam companion for keeping track of tasks. Grab a kopi,
add what's on your plate, and work through it one thing at a time.
Kaya uses friendly phrases while keeping command instructions and error
messages clear.

For automated test commands, coverage reports, and manual checks, see the
[testing guide](Testing.md).

## Adding deadlines

// Describe the action and its outcome.

// Give examples of usage

Example: `keyword (optional arguments)`

// A description of the expected outcome goes here

```
expected output
```

## Feature ABC

// Feature details


## Feature XYZ

// Feature details

## Updating a task

Use `update NUMBER FIELD VALUE` to edit one field of a task. NUMBER is its
one-based position in `list`, not its position in filtered search results.

- `update 1 /description read chapter 2` changes any task's description.
- `update 2 /by 2026-09-20` changes a deadline's due date.
- `update 3 /from 2026-09-21` changes an event's starting date.
- `update 3 /to 2026-09-23` changes an event's ending date.

The task keeps its type, position, completion status, and other details.
Changes are saved automatically using the existing data format.
Dates use `yyyy-MM-dd`. An event may start and end on the same date, but
its end cannot precede its start. Change the end first when moving the
start beyond the current end.

Example response for an incomplete Todo:

```text
All sorted. I've updated this task:
  [T][ ] read chapter 2
```

Missing values, invalid task numbers, unknown or incompatible fields, and
invalid dates produce a `Hmm. ...` error with an explanation, without changing the task.
Only one field is interpreted per command: everything after `/description`
is literal description text, including strings such as `/by`.

## Handling input and file errors

Leading and trailing spaces are ignored. Extra spaces or tabs can separate
command words, date fields, and dates; spacing inside descriptions is preserved.
For deadlines, use `/by` exactly once. For events, use `/from` followed by `/to`,
each exactly once. These date-field tokens are reserved in deadline and event
commands. For example:

```text
deadline return book   /by   2026-09-20
event team meeting   /from   2026-09-20   /to   2026-09-20
```

Dates must exist on the calendar, and an event's end cannot be before its start.
Same-day events and duplicate task descriptions are allowed. Invalid commands
explain what to correct and leave tasks unchanged.

Kaya stores tasks in `data/kaya.txt`, relative to the folder it runs from.
A missing file is normal on the first run and is created when saving a task.
If a save fails, the command is rolled back, including completion status.
Kaya writes a temporary file in the same folder and replaces the saved file only
after the new contents have been written completely. Check the file and folder
permissions before retrying; the data path must be a regular file, not a directory
or symbolic link. If the storage location does not support replacing files safely,
use a local folder.

If startup cannot read the data file, or encounters damaged records, a warning
appears in both the GUI and console. Valid records can still be listed or searched,
but changes are disabled to protect the original file. Close Kaya, back up the
file, fix its contents or permissions (or restore a known good copy), then restart
Kaya. Damaged records include empty descriptions, invalid dates, events that end
before they start, and invalid text encoding.
