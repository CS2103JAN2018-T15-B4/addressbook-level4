package seedu.address.logic.commands;

import java.util.function.Predicate;

import seedu.address.model.person.Person;

/**
 * Finds and lists all persons in address book whose name or tags contains any of the keywords or partial keywords.
 * Keyword matching is case insensitive.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";
    public static final String COMMAND_ALIAS = "f";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds persons whose names or tags contain any of "
            + "the keywords (case-insensitive) and displays them as a list with index numbers.\n"
            + "By Name Parameters: n/KEYWORD [MORE_KEYWORDS]... \n"
            + "By Tag Parameters: t/KEYWORD [MORE_KEYWORDS].. \n"
            + "Example: " + COMMAND_WORD + " n/alice bob charlie \n"
            + "Example: " + COMMAND_WORD + " t/friends";

    private Predicate<Person> predicate;

    //@@author jill858
    public FindCommand(Predicate<Person> predicate) {
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute() {
        model.updateFilteredPersonList(predicate);
        return new CommandResult(getMessageForPersonListShownSummary(model.getFilteredPersonList().size()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof FindCommand // instanceof handles nulls
                && this.predicate.equals(((FindCommand) other).predicate)); // state check
    }
}
