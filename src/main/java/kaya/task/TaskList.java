package kaya.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

    /**
     * Returns tasks whose descriptions contain the keyword, ignoring letter case.
     *
     * @param keyword the text to search for
     * @return matching tasks in their original order
     */
    public List<Task> find(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ENGLISH);
        List<Task> matchingTasks = new ArrayList<>();
        for (Task task : tasks) {
            String normalizedDescription = task.getDescription().toLowerCase(Locale.ENGLISH);
            if (normalizedDescription.contains(normalizedKeyword)) {
                matchingTasks.add(task);
            }
        }
        return matchingTasks;
    }

    /** Returns a copy suitable for display or saving. */
    public List<Task> asList() {
        return new ArrayList<>(tasks);
    }
}
