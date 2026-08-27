package kaya.task;

import java.util.ArrayList;
import java.util.List;

/** Owns Kaya's task collection and its list operations. */
public class TaskList {
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this(new ArrayList<>());
    }

    /**
     * Creates a task list containing tasks loaded from storage.
     *
     * @param tasks the initial tasks
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /** Adds a task to the list. */
    public void add(Task task) {
        tasks.add(task);
    }

    /** Returns the task at the given zero-based index. */
    public Task get(int index) {
        return tasks.get(index);
    }

    /** Removes and returns the task at the given zero-based index. */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /** Returns the number of stored tasks. */
    public int size() {
        return tasks.size();
    }

    /** Returns a copy suitable for display or saving. */
    public List<Task> asList() {
        return new ArrayList<>(tasks);
    }
}
