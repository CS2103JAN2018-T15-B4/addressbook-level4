package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.commands.AddPictureCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new AddPictureCommand object
 */
public class AddPictureCommandParser implements Parser<AddPictureCommand> {

    private String path;
    private Index index;
    /**
     * Parses the given {@code String} of arguments in the context of the DeleteByNameCommand
     * and returns an DeleteByNameCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public AddPictureCommand parse(String args) throws ParseException {


        String[] splitted = args.trim().split("\\s+");

        if (splitted.length != 2) {
            throw new ParseException(
                    String.format("Need exactly 2 args"));
        }

        try {
            index = ParserUtil.parseIndex(splitted[0]);
        } catch (Exception e) {
            throw new ParseException(
                    String.format("Invalid index"));
        }
        path = splitted[1];

        try {
            path = ParserUtil.parseImageFilename(path);
            return new AddPictureCommand(index, path);
        } catch (IllegalValueException ive) {
            ive.printStackTrace();
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddPictureCommand.MESSAGE_USAGE));
        }
    }

}
