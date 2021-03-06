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

//@@author dezhanglee
/**
 * Resets the picture of a contact to the default picture
 */
public class ResetPictureCommand extends Command {

    public static final String COMMAND_WORD = "resetpicture";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Reset Picture for Person: %1$s";
    public static final String MESSAGE_NOT_EDITED = "A valid index must be specified.";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Resets the picture of the person identified by the index number used in the last person listing "
            + "to the default picture.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "Example: " + COMMAND_WORD + " 1 ";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book.";

    private final Index index;

    private Person personToEdit;
    private Person editedPerson;

    public ResetPictureCommand(Index index) {

        requireNonNull(index);

        this.index = index;

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

        editedPerson.resetPicture();

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
                || (other instanceof ResetPictureCommand // instanceof handles nulls
                && this.index.equals(((ResetPictureCommand) other).index)); // state check
    }
}
