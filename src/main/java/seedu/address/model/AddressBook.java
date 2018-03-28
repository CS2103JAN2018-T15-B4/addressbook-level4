package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;
import seedu.address.model.category.TaskCategory;
import seedu.address.model.category.UniqueTaskCategoryList;
import seedu.address.model.person.Person;
import seedu.address.model.person.UniquePersonList;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;
import seedu.address.model.tag.Tag;
import seedu.address.model.tag.UniqueTagList;
import seedu.address.model.task.Task;
import seedu.address.model.task.UniqueTaskList;
import seedu.address.model.task.exceptions.DuplicateTaskException;
import seedu.address.model.task.exceptions.TaskNotFoundException;

/**
 * Wraps all data at the address-book level
 * Duplicates are not allowed (by .equals comparison)
 */
public class AddressBook implements ReadOnlyAddressBook {

    private final UniquePersonList persons;
    private final UniqueTagList tags;
    private final UniqueTaskList tasks;
    private final UniqueTaskCategoryList taskCategories;

    /*
     * The 'unusual' code block below is an non-static initialization block, sometimes used to avoid duplication
     * between constructors. See https://docs.oracle.com/javase/tutorial/java/javaOO/initial.html
     *
     * Note that non-static init blocks are not recommended to use. There are other ways to avoid duplication
     *   among constructors.
     */
    {
        persons = new UniquePersonList();
        tags = new UniqueTagList();
        tasks = new UniqueTaskList();
        taskCategories = new UniqueTaskCategoryList();
    }

    public AddressBook() {}

    /**
     * Creates an AddressBook using the Persons, Tags, Tasks and TaskCategories in the {@code toBeCopied}
     */
    public AddressBook(ReadOnlyAddressBook toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    public void setPersons(List<Person> persons) throws DuplicatePersonException {
        this.persons.setPersons(persons);
    }

    public void setTags(Set<Tag> tags) {
        this.tags.setTags(tags);
    }

    public void setTasks(List<Task> tasks) throws DuplicateTaskException {
        this.tasks.setTasks(tasks);
    }

    public void setTaskCategories(Set<TaskCategory> taskCategories) {
        this.taskCategories.setTaskCategories(taskCategories);
    }

    /**
     * Resets the existing data of this {@code AddressBook} with {@code newData}.
     */
    public void resetData(ReadOnlyAddressBook newData) {
        requireNonNull(newData);
        setTags(new HashSet<>(newData.getTagList()));
        List<Person> syncedPersonList = newData.getPersonList().stream()
                .map(this::syncWithMasterTagList)
                .collect(Collectors.toList());

        setTaskCategories(new HashSet<>(newData.getTaskCategoryList()));
        List<Task> syncedTaskList = newData.getTaskList().stream()
                .map(this::syncWithMasterTaskCategoryList)
                .collect(Collectors.toList());

        try {
            setPersons(syncedPersonList);
            setTasks(syncedTaskList);
        } catch (DuplicatePersonException e) {
            throw new AssertionError("AddressBooks should not have duplicate persons");
        } catch (DuplicateTaskException e) {
            throw new AssertionError("AddressBooks should not have duplicate tasks");
        }
    }

    //// person-level operations

    /**
     * Adds a person to the address book.
     * Also checks the new person's tags and updates {@link #tags} with any new tags found,
     * and updates the Tag objects in the person to point to those in {@link #tags}.
     *
     * @throws DuplicatePersonException if an equivalent person already exists.
     */
    public void addPerson(Person p) throws DuplicatePersonException {
        Person person = syncWithMasterTagList(p);
        // TODO: the tags master list will be updated even though the below line fails.
        // This can cause the tags master list to have additional tags that are not tagged to any person
        // in the person list.
        persons.add(person);
    }

    /**
     * Replaces the given person {@code target} in the list with {@code editedPerson}.
     * {@code AddressBook}'s tag list will be updated with the tags of {@code editedPerson}.
     *
     * @throws DuplicatePersonException if updating the person's details causes the person to be equivalent to
     *      another existing person in the list.
     * @throws PersonNotFoundException if {@code target} could not be found in the list.
     *
     * @see #syncWithMasterTagList(Person)
     */
    public void updatePerson(Person target, Person editedReadOnlyPerson)
            throws DuplicatePersonException, PersonNotFoundException {
        requireNonNull(editedReadOnlyPerson);

        Person editedPerson = new Person(editedReadOnlyPerson);
        syncWithMasterTagList(editedPerson);
        // TODO: the tags master list will be updated even though the below line fails.
        // This can cause the tags master list to have additional tags that are not tagged to any person
        // in the person list.
        persons.setPerson(target, editedPerson);
    }

    /**
     *  Updates the master tag list to include tags in {@code person} that are not in the list.
     *  @return a copy of this {@code person} such that every tag in this person points to a Tag object in the master
     *  list.
     */
    private Person syncWithMasterTagList(Person person) {
        final UniqueTagList personTags = new UniqueTagList(person.getTags());
        tags.mergeFrom(personTags);

        // Create map with values = tag object references in the master list
        // used for checking person tag references
        final Map<Tag, Tag> masterTagObjects = new HashMap<>();
        tags.forEach(tag -> masterTagObjects.put(tag, tag));

        // Rebuild the list of person tags to point to the relevant tags in the master tag list.
        final Set<Tag> correctTagReferences = new HashSet<>();
        personTags.forEach(tag -> correctTagReferences.add(masterTagObjects.get(tag)));
        return new Person(
                person.getName(), person.getPhone(), person.getEmail(), person.getAddress(),
                person.getPicture(), correctTagReferences);
    }

    /**
     * Removes {@code key} from this {@code AddressBook}.
     * @throws PersonNotFoundException if the {@code key} is not in this {@code AddressBook}.
     */
    public boolean removePerson(Person key) throws PersonNotFoundException {
        if (persons.remove(key)) {
            return true;
        } else {
            throw new PersonNotFoundException();
        }
    }

    /**
     * Sorts the person list alphabetically by name
     */
    public void sortPersons() {
        persons.sort();
    }

    //// tag-level operations

    public void addTag(Tag t) throws UniqueTagList.DuplicateTagException {
        tags.add(t);
    }

    //// task-level operations
    /**
     * Adds a task to the address book.
     * Also checks the new task's categories and updates {@link #taskCategories} with any new taskCategories found,
     * and updates the TaskCategory objects in the task to point to those in {@link #taskCategories}.
     *
     * @throws DuplicateTaskException if an equivalent task already exists.
     */
    public void addTask(Task t) throws DuplicateTaskException {
        Task task = syncWithMasterTaskCategoryList(t);
        // TODO: the taskCategories master list will be updated even though the below line fails.
        // This can cause the taskCategories master list to have additional taskCategories that are not tagged to
        // any task in the task list.
        tasks.add(task);
    }

    /**
     * Replaces the given task {@code target} in the list with {@code editedTask}.
     * {@code AddressBook}'s taskCategory list will be updated with the taskCategories of {@code editedTask}.
     *
     * @throws DuplicateTaskException if updating the task's details causes the task to be equivalent to
     * another existing task in the list.
     * @throws TaskNotFoundException if {@code target} could not be found in the list.
     *
     * @see #syncWithMasterTaskCategoryList(Task)
     */
    public void updateTask(Task target, Task editedTask) throws DuplicateTaskException, TaskNotFoundException {
        requireNonNull(editedTask);

        Task syncedEditedTask = syncWithMasterTaskCategoryList(editedTask);
        // TODO: the taskCategories master list will be updated even though the below line fails.
        // This can cause the taskCategories master list to have additional taskCategories that are not tagged to
        // any task in the person list.
        tasks.setTask(target, syncedEditedTask);
    }

    /**
     * Updates the master taskCategory list to include taskCategories in {@code task} that are not in the list.
     * @return a copy of this {@code task} such that every taskCategory in this task points to a TaskCategory object
     * in the master list.
     */
    private Task syncWithMasterTaskCategoryList(Task task) {
        final UniqueTaskCategoryList categories = new UniqueTaskCategoryList(task.getTaskCategories());
        taskCategories.mergeFrom(categories);

        // Create map with values = taskCategory object references in the master list
        // Used for checking task category references
        final Map<TaskCategory, TaskCategory> masterTaskCategoryObjects = new HashMap<>();
        taskCategories.forEach(taskCategory -> masterTaskCategoryObjects.put(taskCategory, taskCategory));

        // Rebuild the list of task categories to point to the relevant taskCategories in the master taskCategory list.
        final Set<TaskCategory> correctTaskCategoryReferences = new HashSet<>();
        categories.forEach(taskCategory -> correctTaskCategoryReferences.add(
                masterTaskCategoryObjects.get(taskCategory)));
        return new Task(task.getTaskName(), task.getTaskPriority(), task.getTaskDescription(), task.getTaskDueDate(),
                task.getTaskStatus(), correctTaskCategoryReferences);
    }

    /**
     * Removes {@code key} from this {@code AddressBook}.
     * @throws TaskNotFoundException if the {@code key} is not in this {@code AddressBook}.
     */
    public boolean removeTask(Task key) throws TaskNotFoundException {
        if (tasks.remove(key)) {
            return true;
        } else {
            throw new TaskNotFoundException();
        }
    }

    /**
     * Sorts the task list
     */
    public void sortTasks() {
        // TODO: Implement sort
    }

    //// taskCategory-level operations

    public void addTaskCategory(TaskCategory tc) throws UniqueTaskCategoryList.DuplicateTaskCategoryException {
        taskCategories.add(tc);
    }

    //// util methods

    @Override
    public String toString() {
        return persons.asObservableList().size() + " persons, " + tags.asObservableList().size() +  " tags, "
                + tasks.asObservableList().size() + " tasks, " + taskCategories.asObservableList().size()
                + " task categories";
        // TODO: refine later
    }

    @Override
    public ObservableList<Person> getPersonList() {
        return persons.asObservableList();
    }

    @Override
    public ObservableList<Tag> getTagList() {
        return tags.asObservableList();
    }

    @Override
    public ObservableList<Task> getTaskList() {
        return tasks.asObservableList();
    }

    @Override
    public ObservableList<TaskCategory> getTaskCategoryList() {
        return taskCategories.asObservableList();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof AddressBook // instanceof handles nulls
                && this.persons.equals(((AddressBook) other).persons)
                && this.tags.equalsOrderInsensitive(((AddressBook) other).tags)
                && this.tasks.equals(((AddressBook) other).tasks)
                && this.taskCategories.equalsOrderInsensitive(((AddressBook) other).taskCategories));
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(persons, tags, tasks, taskCategories);
    }

}
