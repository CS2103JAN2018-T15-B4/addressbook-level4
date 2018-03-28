package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.person.Person;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;

/**
 * Adds a profile picture to a person in the address book
 */
public class AddPictureCommand extends Command {

    public static final String COMMAND_WORD = "addpicture";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited Picture for Person: %1$s";
    public static final String MESSAGE_NOT_EDITED = "A valid file must be provided.";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Edits the picture of the person identified by the index number used in the last person listing.\n"
            + "Parameters: INDEX (must be a positive integer) FILEPATH (path to a valid image file)\n"
            + "Example: " + COMMAND_WORD + " 1 C://Pictures/johnPicture.jpg";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book.";

    private final Index index;
    private final String path;

    private Person personToEdit;
    private Person editedPerson;

    public AddPictureCommand(Index index, String path) {

        requireNonNull(index);
        requireNonNull(path);

        this.index = index;
        this.path = path;

    }

    @Override
    public CommandResult execute() throws CommandException {

        List<Person> lastShownList = model.getFilteredPersonList();

        int personIndex = index.getZeroBased();

        if (personIndex >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        personToEdit = lastShownList.get(personIndex);
        editedPerson = new Person(personToEdit);

        try {
            editedPerson.setPicture(path);
        } catch (Exception e) {
            throw new CommandException(MESSAGE_NOT_EDITED);
        }

        try {
            model.updatePerson(personToEdit, editedPerson);
        } catch (DuplicatePersonException dpe) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        } catch (PersonNotFoundException pnfe) {
            throw new AssertionError("The target person cannot be missing");
        }

        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_EDIT_PERSON_SUCCESS, index.getOneBased()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof AddPictureCommand // instanceof handles nulls
                && this.index.equals(((AddPictureCommand) other).index)
                && this.path.equals(((AddPictureCommand) other).path)); // state check
    }
}
