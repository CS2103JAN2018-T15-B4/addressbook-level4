package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.commons.core.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Name;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.person.exceptions.PersonNotFoundException;

/**
 * Deletes a person by name. Case insensitive
 * Will not work if there are multiple people with same names, use DeleteCommand instead
 */

public class DeleteByNameCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "deletebyname";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the person identified by the input NAME. Non case sensitive.\n"
            + "Parameters: NAME\n"
            + "Example: " + COMMAND_WORD + " John";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted Person: %1$s";

    public static final String MESSAGE_NAME_NOT_FOUND = "Person with input name not found. "
            + "Please check if the person exists using the list command - \n"
            + ListCommand.MESSAGE_USAGE;

    public static final String MESSAGE_MULTIPLE_SAME_NAME = "There are several people with the same name."
            + "Please use the delete command instead -  \n"
            + DeleteCommand.MESSAGE_USAGE;


    private Name inputName;
    private List<Person> allPersons, personsWithMatchingName;
    private Person personToBeDeleted;



    public DeleteByNameCommand(Name inputName) { this.inputName = inputName; }

    public CommandResult executeUndoableCommand() throws CommandException {

        searchForPersonByName();

        if (personsWithMatchingName.size() == 0) {
            throw new CommandException(MESSAGE_NAME_NOT_FOUND);
        }

        else if (personsWithMatchingName.size() > 1) {
            throw new CommandException(MESSAGE_MULTIPLE_SAME_NAME);
        }

        try {
            personToBeDeleted = personsWithMatchingName.get(0);
            model.deletePerson(personToBeDeleted);
        } catch (PersonNotFoundException pnfe) {
            throw new AssertionError("The target person cannot be missing");
        }

        return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS, personToBeDeleted));
    }

    /**
     * Helper function: Extract and search PersonList for the name
     */

    public void searchForPersonByName() {

        this.allPersons = model.getAddressBook().getPersonList();

        Stream<Person> searchPerson = allPersons.stream()
                .filter(person -> person.getName().toString().toLowerCase()
                        .equals(inputName.toString().toLowerCase())); //

        this.personsWithMatchingName = searchPerson.collect(Collectors.toList());

    }



}
