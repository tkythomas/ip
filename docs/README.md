# Kaya User Guide

// Update the title above to match the actual product name

// Product screenshot goes here

// Product intro goes here

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
Got it. I've updated this task:
  [T][ ] read chapter 2
```

Missing values, invalid task numbers, unknown or incompatible fields, and
invalid dates produce an `OOPS!!!` error without changing the task.
Only one field is interpreted per command: everything after `/description`
is literal description text, including strings such as `/by`.
