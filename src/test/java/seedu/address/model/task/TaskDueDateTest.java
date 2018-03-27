package seedu.address.model.task;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import seedu.address.testutil.Assert;

public class TaskDueDateTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> new TaskDueDate(null));
    }

    @Test
    public void constructor_invalidTaskDueDate_throwsIllegalArgumentException() {
        String invalidDueDate = "";
        Assert.assertThrows(IllegalArgumentException.class, () -> new TaskDueDate(invalidDueDate));
    }

    @Test
    public void isValidTaskDueDate() {
        // null task due date
        Assert.assertThrows(NullPointerException.class, () -> TaskDueDate.isValidTaskDueDate(null));

        // invalid task due date
        assertFalse(TaskDueDate.isValidTaskDueDate("")); // empty string
        assertFalse(TaskDueDate.isValidTaskDueDate(" ")); // spaces only
        assertFalse(TaskDueDate.isValidTaskDueDate("random xyz")); // random string
        assertFalse(TaskDueDate.isValidTaskDueDate("12345")); // numbers
        assertFalse(TaskDueDate.isValidTaskDueDate("random 123")); // alphanumeric

        // invalid task due date format
        assertFalse(TaskDueDate.isValidTaskDueDate("2018 05 05")); // without formatter
        assertFalse(TaskDueDate.isValidTaskDueDate("2018-02-29")); // date existing in leap year only
        assertFalse(TaskDueDate.isValidTaskDueDate("2018-12-32")); // non-existing date
        assertFalse(TaskDueDate.isValidTaskDueDate("1989-12-31")); // valid year starts from 1900

        // valid task due date
        assertTrue(TaskDueDate.isValidTaskDueDate("2018-05-05")); // correct format
        assertTrue(TaskDueDate.isValidTaskDueDate("2018-12-31")); // existing date
        assertTrue(TaskDueDate.isValidTaskDueDate("2020-02-29")); // date in leap year
    }

}
