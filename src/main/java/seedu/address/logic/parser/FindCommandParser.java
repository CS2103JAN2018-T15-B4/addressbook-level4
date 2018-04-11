package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Arrays;
import java.util.List;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.AddressContainsKeywordsPredicate;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.PhoneContainsKeywordsPredicate;
import seedu.address.model.tag.TagContainsKeywordsPredicate;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns an FindCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        List<Prefix> prefixLists = Arrays.asList(PREFIX_NAME, PREFIX_TAG);

        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        //@@author jill858
        String targetPrefix = getPrefix(trimmedArgs);
        String[] keywords = getKeywords(trimmedArgs);

        if (targetPrefix.equals(PREFIX_NAME.getPrefix())) {
            return new FindCommand(new NameContainsKeywordsPredicate(Arrays.asList(keywords)));
        } else if (targetPrefix.equals(PREFIX_TAG.getPrefix())) {
            return new FindCommand(new TagContainsKeywordsPredicate(Arrays.asList(keywords)));
        } else if (targetPrefix.equals(PREFIX_ADDRESS.getPrefix())) {
            return new FindCommand(new AddressContainsKeywordsPredicate(Arrays.asList(keywords)));
        } else if (targetPrefix.equals(PREFIX_PHONE.getPrefix())) {
            return new FindCommand(new PhoneContainsKeywordsPredicate(Arrays.asList(keywords)));
        } else {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }
    }

    //@@author jill858
    /**
     * Extract the search type (
     * @param args command line input
     * @return type of search field
     */
    private static String getPrefix(String args) {
        return args.substring(0, 2);
    }

    //@@author jill858
    /**
     * Extract keywords out from the command input
     * @param args command line input
     * @return list of keywords
     */
    private static String[] getKeywords(String args) {
        String[] keywords;

        String removePrefixKeywords = args.substring(2);

        return keywords = removePrefixKeywords.split("\\s+");
    }

}
