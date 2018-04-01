package seedu.address.logic.commands;

import static seedu.address.model.Model.PREDICATE_SHOW_ALL_EVENTS;

//@@author x3tsunayh

/**
 * Lists all events in the event book to the user.
 */
public class ListAllEventCommand extends Command {
    public static final String COMMAND_WORD = "listevent";

    public static final String MESSAGE_SUCCESS = "Listed all events!";

    @Override
    public CommandResult execute() {
        model.updateFilteredEventList(PREDICATE_SHOW_ALL_EVENTS);
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
