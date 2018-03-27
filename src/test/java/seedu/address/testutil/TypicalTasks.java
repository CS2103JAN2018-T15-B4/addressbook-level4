package seedu.address.testutil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import seedu.address.model.task.Task;

/**
 * A utility class containing a list of {@code Task} objects to be used in tests.
 */
public class TypicalTasks {

    public static final Task TASKONE = new TaskBuilder().withTaskName("Task 1")
            .withTaskPriority("Medium")
            .withTaskDescription("Tasks to be done for task 1")
            .withTaskDueDate("2018-06-15")
            .withTaskStatus("Undone")
            .withTaskCategories("Work").build();
    public static final Task TASKTWO = new TaskBuilder().withTaskName("Task 2")
            .withTaskPriority("High")
            .withTaskDescription("Agenda for task 2")
            .withTaskDueDate("2018-03-27")
            .withTaskStatus("Undone")
            .withTaskCategories("Personal").build();
    public static final Task TASKTHREE = new TaskBuilder().withTaskName("Task 3")
            .withTaskPriority("Low")
            .withTaskDescription("Purchase office supplies")
            .withTaskDueDate("2018-04-10")
            .withTaskStatus("Undone")
            .withTaskCategories("Work").build();

    // Manually added
    public static final Task TASKFOUR = new TaskBuilder().withTaskName("Task 4")
            .withTaskPriority("Medium")
            .withTaskDescription("Work in progress: Development and documentation")
            .withTaskDueDate("2018-05-10")
            .withTaskStatus("Undone").build();

    private TypicalTasks() {} // prevents instantiation

    public static List<Task> getTypicalTasks() {
        return new ArrayList<>(Arrays.asList(TASKONE, TASKTWO, TASKTHREE));
    }

}
