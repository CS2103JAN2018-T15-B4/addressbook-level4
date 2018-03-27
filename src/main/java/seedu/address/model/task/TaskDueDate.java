package seedu.address.model.task;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.time.LocalDate;

/**
 * Represents a Task's due date in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidTaskDueDate(String)}
 */
public class TaskDueDate {

    public static final String MESSAGE_TASK_DUE_DATE_CONSTRAINTS =
            "Task due dates must be a valid date in the format yyyy-MM-dd and after today's date";

    /**
     * The first character of the task due date must not be a whitespace,
     * otherwise " " (a blank string) becomes a valid input.
     * This regex checks for years from 1900 to 9999 and also leap year.
     */
    public static final String TASK_DUE_DATE_VALIDATION_REGEX =
            "^((2000|2400|2800|(19|2[0-9](0[48]|[2468][048]|[13579][26])))-02-29)$|"
                    + "^(((19|[2-9][0-9])[0-9]{2})-02-(0[1-9]|1[0-9]|2[0-8]))$|"
                    + "^(((19|[2-9][0-9])[0-9]{2})-(0[13578]|10|12)-(0[1-9]|[12][0-9]|3[01]))$|"
                    + "^(((19|[2-9][0-9])[0-9]{2})-(0[469]|11)-(0[1-9]|[12][0-9]|30))$";

    public final String value;

    /**
     * Constructs a {@code TaskDueDate}.
     *
     * @param taskDueDate A valid task due date.
     */
    public TaskDueDate(String taskDueDate) {
        requireNonNull(taskDueDate);
        checkArgument(isValidTaskDueDate(taskDueDate), MESSAGE_TASK_DUE_DATE_CONSTRAINTS);
        this.value = taskDueDate;
    }

    /**
     * Returns true if a given string is a valid task due date.
     */
    public static boolean isValidTaskDueDate(String test) {
        return test.matches(TASK_DUE_DATE_VALIDATION_REGEX) && isTaskDueDateAfterTodayDate(test);
    }

    /**
     * Validates that the task due date is after today's date.
     *
     * @param testDueDate A valid task due date.
     * @return true if a given task due date is after today's date.
     */
    public static boolean isTaskDueDateAfterTodayDate(String testDueDate) {
        LocalDate dueDate = LocalDate.parse(testDueDate);
        LocalDate today = LocalDate.now();

        return dueDate.isAfter(today);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof TaskDueDate
                && this.value.equals(((TaskDueDate) other).value));
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
