# x3tsunayh
###### \java\seedu\address\commons\events\model\EventBookChangedEvent.java
``` java

/**
 * Indicates the EventBook in the model has changed
 */
public class EventBookChangedEvent extends BaseEvent {

    public final ReadOnlyEventBook data;

    public EventBookChangedEvent(ReadOnlyEventBook data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "number of events " + data.getEventList().size();
    }
}
```
###### \java\seedu\address\commons\events\ui\EventPanelSelectionChangedEvent.java
``` java

/**
 * Represents a selection change in the Event List Panel
 */
public class EventPanelSelectionChangedEvent extends BaseEvent {

    private final EventCard newSelection;

    public EventPanelSelectionChangedEvent(EventCard newSelection) {
        this.newSelection = newSelection;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    public EventCard getNewSelection() {
        return newSelection;
    }

}
```
###### \java\seedu\address\commons\util\FileUtil.java
``` java

    /**
     * Checks if a String in {@code filePath} is a valid XML file
     */
    public static boolean isValidXmlFile(String filePath) {
        return filePath.toLowerCase().matches(REGEX_XML_FILE);
    }

    /**
     * Checks if a String in {@code filePath} is a valid CSV file
     */
    public static boolean isValidCsvFile(String filePath) {
        return filePath.toLowerCase().matches(REGEX_CSV_FILE);
    }

```
###### \java\seedu\address\logic\commands\AddEventCommand.java
``` java

/**
 * Adds an event to event book.
 */
public class AddEventCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "addevent";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds an event to the event book. "
            + "Parameters: "
            + PREFIX_EVENT_TITLE + "NAME "
            + PREFIX_EVENT_DESCRIPTION + "DESCRIPTION "
            + PREFIX_EVENT_LOCATION + "LOCATION "
            + PREFIX_EVENT_DATETIME + "DATETIME\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_EVENT_TITLE + "Movie Outing "
            + PREFIX_EVENT_DESCRIPTION + "Watching Black Panther "
            + PREFIX_EVENT_LOCATION + "Suntec City GV "
            + PREFIX_EVENT_DATETIME + "22-04-2018 1630";

    public static final String MESSAGE_SUCCESS = "New event added: %1$s";
    public static final String MESSAGE_DUPLICATE_EVENT = "This event already exists in the event book";

    private final ReadOnlyEvent toAdd;

    /**
     * Creates an AddEventCommand to add the specified {@code ReadOnlyEvent}
     */
    public AddEventCommand(ReadOnlyEvent event)  {
        requireNonNull(event);
        toAdd = new Event(event);
    }

    @Override
    protected CommandResult executeUndoableCommand() throws CommandException {
        requireNonNull(model);
        try {
            model.addEvent(toAdd);
            return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd));
        } catch (Error e) {
            throw new CommandException(MESSAGE_DUPLICATE_EVENT);
        }
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof AddEventCommand // instanceof handles nulls
                && toAdd.equals(((AddEventCommand) other).toAdd));
    }
}
```
###### \java\seedu\address\logic\commands\DeleteEventCommand.java
``` java

/**
 * Deletes a event identified using its last displayed index from the latest displayed event list.
 */
public class DeleteEventCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "deleteevent";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes event identified by the index number used in the last event listing.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_DELETE_EVENT_SUCCESS = "Deleted Event: %1$s";

    private final Index targetIndex;

    public DeleteEventCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    protected CommandResult executeUndoableCommand() throws CommandException {
        List<ReadOnlyEvent> lastShownList = model.getFilteredEventList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_EVENT_DISPLAYED_INDEX);
        }

        ReadOnlyEvent eventToDelete = lastShownList.get(targetIndex.getZeroBased());

        try {
            model.deleteEvent(eventToDelete);
        } catch (Error e) {
            assert false : "Event cannot be empty.";
        }

        return new CommandResult(String.format(MESSAGE_DELETE_EVENT_SUCCESS, eventToDelete));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof DeleteEventCommand // instanceof handles nulls
                && this.targetIndex.equals(((DeleteEventCommand) other).targetIndex));
    }
}
```
###### \java\seedu\address\logic\commands\ExportCommand.java
``` java

/**
 * Exports the address book to a user-defined location {@code filePath}
 */
public class ExportCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "export";
    public static final String COMMAND_ALIAS = "exp";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Exports current data into defined file path. "
            + "Parameters: FILEPATH (must end with an extension of .xml or .csv)\n"
            + "Example: " + COMMAND_WORD + " "
            + " C:\\Users\\John Doe\\Documents\\addressbook.xml\n";

    public static final String MESSAGE_EXPORT_SUCCESS = "Addressbook data exported to: %1$s";
    public static final String MESSAGE_NOT_XML_CSV_FILE = "Filepath does not lead to an XML/CSV file.";
    public static final String MESSAGE_ERROR = "Addressbook data not exported successfully.";
    public static final String MESSAGE_EXISTING_XML = "XML/CSV file name already exists. Choose a different name.";

    private Storage storage;
    private final String filePath;

    /**
     * Creates an ExportCommand to add the specified {@code String}
     */
    public ExportCommand(String filePath) {
        this.filePath = filePath;
    }

    /**
     * @param storage
     */
    @Override
    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        if (FileUtil.isValidCsvFile(filePath)) {
            try {
                storage.exportAddressBookCsv(model.getAddressBook(), filePath);
            } catch (IOException e) {
                throw new CommandException(MESSAGE_ERROR);
            } catch (InvalidFileException e) {
                throw new CommandException(MESSAGE_NOT_XML_CSV_FILE);
            } catch (ExistingFileException e) {
                throw new CommandException(MESSAGE_EXISTING_XML);
            }
            return new CommandResult(String.format(MESSAGE_EXPORT_SUCCESS, filePath));
        }

        try {
            storage.exportAddressBook(model.getAddressBook(), filePath);
        } catch (IOException e) {
            throw new CommandException(MESSAGE_ERROR);
        } catch (InvalidFileException e) {
            throw new CommandException(MESSAGE_NOT_XML_CSV_FILE);
        } catch (ExistingFileException e) {
            throw new CommandException(MESSAGE_EXISTING_XML);
        }
        return new CommandResult(String.format(MESSAGE_EXPORT_SUCCESS, filePath));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ExportCommand // instanceof handles nulls
                && filePath.equals(((ExportCommand) other).filePath));
    }
}

```
###### \java\seedu\address\logic\commands\FindEventCommand.java
``` java

/**
 * Finds and lists all events in event book whose name contains any of the argument keywords.
 * Keyword matching is case sensitive, and only one field can be searched.
 */
public class FindEventCommand extends Command {

    public static final String COMMAND_WORD = "findevent";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Finds all events whose title or description contain any "
            + "of the specified keywords (case-sensitive) and displays them as a list with index numbers.\n"
            + "Parameters: et/KEYWORD [MORE_KEYWORDS]... for title OR ed/KEYWORD [MORE_KEYWORDS]... for description\n"
            + "Example: " + COMMAND_WORD + " et/movie date party";

    private final TitleContainsKeywordsPredicate predicate;

    public FindEventCommand(TitleContainsKeywordsPredicate predicate) {
        this.predicate = predicate;
    }

    public static String getCommandWord() {
        return COMMAND_WORD;
    }

    @Override
    public CommandResult execute() {
        model.updateFilteredEventList(predicate);
        return new CommandResult(getMessageForEventListShownSummary(model.getFilteredEventList().size()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof FindEventCommand // instanceof handles nulls
                && this.predicate.equals(((FindEventCommand) other).predicate));
    }
}
```
###### \java\seedu\address\logic\commands\ListAllEventsCommand.java
``` java

/**
 * Lists all events in the event book to the user.
 */
public class ListAllEventsCommand extends Command {
    public static final String COMMAND_WORD = "listallevents";
    public static final String COMMAND_WORD_TWO = "listevents";
    public static final String COMMAND_WORD_THREE = "allevents";

    @Override
    public CommandResult execute() {
        model.updateFilteredEventList(PREDICATE_SHOW_ALL_EVENTS);
        return new CommandResult("All "
                + getMessageForEventListShownSummary(model.getFilteredEventList().size()));
    }
}
```
###### \java\seedu\address\logic\commands\SwitchTabCommand.java
``` java

/**
 * switch between the Events and Tasks list
 */
public class SwitchTabCommand extends Command {

    public static final String COMMAND_WORD = "switchtab";
    public static final String COMMAND_WORD_TWO = "switch";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Switches between Event and Task UI List Tab";
    public static final String MESSAGE_SUCCESS = "Switched Tabs!";

    private static final int EVENTS_TAB = 0;
    private static final int TASKS_TAB = 1;

    private final TabPane tabPane;

    public SwitchTabCommand(TabPane tabPane) {
        this.tabPane = tabPane;
    }

    @Override
    public CommandResult execute() {
        SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
        int selectedIndex = selectionModel.getSelectedIndex();
        if (selectedIndex == EVENTS_TAB) {
            selectedIndex = TASKS_TAB;
        } else {
            selectedIndex = EVENTS_TAB;
        }
        selectionModel.select(selectedIndex);
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
```
###### \java\seedu\address\logic\LogicManager.java
``` java
    @Override
    public void setCalendarView(CalendarView calendarView) {
        this.calendarViewStateParser = new CalendarViewStateParser(this.userPrefs, this.model, calendarView);
    }

    @Override
    public ObservableList<ReadOnlyEvent> getFilteredEventList() {
        return model.getFilteredEventList();
    }

    @Override
    public void setTabPane(TabPane tabPane) {
        addressBookParser.setTabPane(tabPane);
    }
}
```
###### \java\seedu\address\logic\parser\AddEventCommandParser.java
``` java

/**
 * Parses input arguments and creates a new AddEventCommand object
 */
public class AddEventCommandParser implements Parser<AddEventCommand> {

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

    /**
     * Parses the given {@code String} of arguments in the context of the AddEventCommand
     * and returns an AddEventCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    @Override
    public AddEventCommand parse(String args) throws IllegalValueException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args,
                        PREFIX_EVENT_TITLE, PREFIX_EVENT_DESCRIPTION, PREFIX_EVENT_LOCATION, PREFIX_EVENT_DATETIME);

        if (!arePrefixesPresent(argMultimap,
                PREFIX_EVENT_TITLE, PREFIX_EVENT_DESCRIPTION, PREFIX_EVENT_LOCATION, PREFIX_EVENT_DATETIME)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddEventCommand.MESSAGE_USAGE));
        }

        String title = argMultimap.getValue(PREFIX_EVENT_TITLE).get();
        String description = argMultimap.getValue(PREFIX_EVENT_DESCRIPTION).get();
        String location = argMultimap.getValue(PREFIX_EVENT_LOCATION).get();
        Datetime datetime = ParserUtil.parseDatetime(argMultimap.getValue(PREFIX_EVENT_DATETIME)).get();

        ReadOnlyEvent event = new Event(title, description, location, datetime);

        return new AddEventCommand(event);
    }
}
```
###### \java\seedu\address\logic\parser\CalendarViewStateParser.java
``` java

/**
* Parses user input to update calendar UI state.
*/
public class CalendarViewStateParser {

    private static UserPrefs userPrefs;
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");
    private final Model model;
    private CalendarView calendarView;

    public CalendarViewStateParser(UserPrefs userPrefs, Model model, CalendarView calendarView) {
        this.userPrefs = userPrefs;
        this.model = model;
        this.calendarView = calendarView;
    }

    /**
     * update the state of the calendar UI object with reference to the user input
     *
     * @param userInput
     * @throws ParseException
     */
    public void updateViewState(String userInput) throws ParseException {

        //Checks whether CalendarView is null
        requireNonNull(calendarView);

        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        final String commandWord = matcher.group("commandWord");

        if (commandWord.equals(AddEventCommand.COMMAND_WORD)
                || commandWord.equals(DeleteEventCommand.COMMAND_WORD)
                //|| commandWord.equals(EditEventCommand.COMMAND_WORD)
                //|| commandWord.equals(ListEventCommand.COMMAND_WORD)
                //|| commandWord.equals(OrderEventCommand.COMMAND_WORD)
                || commandWord.equals(ClearCommand.COMMAND_WORD)
                || commandWord.equals(ClearCommand.COMMAND_ALIAS)
                || commandWord.equals(UndoCommand.COMMAND_WORD)
                || commandWord.equals(UndoCommand.COMMAND_ALIAS)
                || commandWord.equals(RedoCommand.COMMAND_WORD)
                || commandWord.equals(RedoCommand.COMMAND_ALIAS)) {
            CalendarViewUpdate.updateViewState(calendarView);
        } else if (commandWord.equals(FindEventCommand.COMMAND_WORD)) {
            CalendarViewUpdate.updateFindState(calendarView, model);
        }
    }
}
```
###### \java\seedu\address\logic\parser\CliSyntax.java
``` java
    public static final Prefix PREFIX_EVENT_TITLE = new Prefix("et/");
    public static final Prefix PREFIX_EVENT_DESCRIPTION = new Prefix("ed/");
    public static final Prefix PREFIX_EVENT_LOCATION = new Prefix("el/");
    public static final Prefix PREFIX_EVENT_DATETIME = new Prefix("edt/");

```
###### \java\seedu\address\logic\parser\DeleteEventCommandParser.java
``` java

/**
 * Parses input arguments and creates a new DeleteEventCommand object
 */
public class DeleteEventCommandParser implements Parser<DeleteEventCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the DeleteEventCommand
     * and returns an DeleteEventCommand object for execution.
     *
     * @throws ParseException if the user input is not of acceptable format
     */
    @Override
    public DeleteEventCommand parse(String args) throws ParseException {
        try {
            Index index = ParserUtil.parseIndex(args);
            return new DeleteEventCommand(index);
        } catch (IllegalValueException ive) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteEventCommand.MESSAGE_USAGE));
        }
    }
}
```
###### \java\seedu\address\logic\parser\FindEventCommandParser.java
``` java

/**
 * Parses input arguments and creates a new FindEventCommand object
 */
public class FindEventCommandParser implements Parser<FindEventCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FindEventCommand
     * and returns an FindCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public FindEventCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindEventCommand.MESSAGE_USAGE));
        }
        if (trimmedArgs.substring(0, 2).equals("et")) {
            TitleContainsKeywordsPredicate.setPredicateType("et");
        } else if (trimmedArgs.substring(0, 3).equals("edt")) {
            TitleContainsKeywordsPredicate.setPredicateType("edt");
        } else if (trimmedArgs.substring(0, 2).equals("ed")) {
            TitleContainsKeywordsPredicate.setPredicateType("ed");
        } else if (trimmedArgs.substring(0, 2).equals("em")) {
            TitleContainsKeywordsPredicate.setPredicateType("em");
        } else {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindEventCommand.MESSAGE_USAGE));
        }

        if (trimmedArgs.substring(0, 3).equals("edt")) {
            trimmedArgs = trimmedArgs.substring(4).trim();
        } else {
            trimmedArgs = trimmedArgs.substring(3).trim();
        }

        String[] titleKeywords = trimmedArgs.split("\\s+");

        return new FindEventCommand(new TitleContainsKeywordsPredicate(Arrays.asList(titleKeywords)));
    }
}
```
###### \java\seedu\address\model\event\Datetime.java
``` java

/**
 * Represents an Event's Datetime in the event book.
 * Ensures valid Datetime input and aids future implementations involving NLP, etc.
 */
public class Datetime {

    public static final String MESSAGE_DATETIME_CONSTRAINTS =
            "Event datetime should be in the format: dd-mm-yyyy hhmm";

    private static final int VALID_DATETIME_LENGTH = 15;

    public final String value;

    /**
     * Validates given datetime.
     *
     * @throws IllegalValueException if given datetime string is invalid.
     */
    public Datetime(String datetime) throws IllegalValueException {
        requireNonNull(datetime);
        String trimmedDatetime = datetime.trim();

        if (!isValidDatetime(trimmedDatetime)) {
            throw new IllegalValueException(MESSAGE_DATETIME_CONSTRAINTS);
        }
        this.value = trimmedDatetime;
    }

    /**
     * Returns true if a given string is a valid event datetime.
     */
    public static boolean isValidDatetime(String test) {
        Boolean validTime = false;
        Boolean validDate = false;

        if (test.length() != VALID_DATETIME_LENGTH) {
            return false;
        }

        try {
            int day = Integer.parseInt(test.substring(0, 2));
            int month = Integer.parseInt(test.substring(3, 5));
            int year = Integer.parseInt(test.substring(6, 10));
            int hour = Integer.parseInt(test.substring(11, 13));
            int min = Integer.parseInt(test.substring(13, 15));

            //Time Validation
            if (0 <= hour && hour <= 23) {
                if (0 <= min && min <= 59) {
                    validTime = true;
                }
            }

            //Date Validation
            if (day >= 1) {
                // For months with 30 days.
                if ((month == 4
                        || month == 6
                        || month == 9
                        || month == 11)
                        && day <= 30) {
                    validDate = true;
                }
                // For months with 31 days.
                if ((month == 1
                        || month == 3
                        || month == 5
                        || month == 7
                        || month == 8
                        || month == 10
                        || month == 12)
                        && day <= 31) {
                    validDate = true;
                }
                // For February.
                if (month == 2) {
                    if (day <= 28) {
                        validDate = true;
                    } else if (day == 29) {
                        if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
                            validDate = true;
                        }
                    }
                }
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return validTime && validDate;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Datetime
                && this.value.equals(((Datetime) other).value));
    }

    @Override
    public String toString() {
        return value;
    }
}
```
###### \java\seedu\address\model\event\Event.java
``` java

/**
 * Represents an Event in the event book.
 * Makes sure all fields are filled and not null.
 */
public class Event implements ReadOnlyEvent {

    private ObjectProperty<String> title;
    private ObjectProperty<String> description;
    private ObjectProperty<String> location;
    private ObjectProperty<Datetime> datetime;

    /**
     * Every field must be present and not null.
     */
    public Event(String title, String description, String location, Datetime datetime) {
        requireAllNonNull(title, description, location, datetime);
        this.title = new SimpleObjectProperty<>(title);
        this.description = new SimpleObjectProperty<>(description);
        this.location = new SimpleObjectProperty<>(location);
        this.datetime = new SimpleObjectProperty<>(datetime);
    }

    /**
     * Creates a copy of the given ReadOnlyEvent.
     * This prevents the original version from being changed unknowingly.
     */
    public Event(ReadOnlyEvent source) {
        this(source.getTitle(), source.getDescription(), source.getLocation(), source.getDatetime());
    }

    @Override
    public ObjectProperty<String> titleProperty() {
        return title;
    }

    @Override
    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(requireNonNull(title));
    }

    @Override
    public ObjectProperty<String> descriptionProperty() {
        return description;
    }

    @Override
    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(requireNonNull(description));
    }

    @Override
    public ObjectProperty<String> locationProperty() {
        return location;
    }

    @Override
    public String getLocation() {
        return location.get();
    }

    public void setLocation(String location) {
        this.location.set(requireNonNull(location));
    }

    @Override
    public ObjectProperty<Datetime> datetimeProperty() {
        return datetime;
    }

    @Override
    public Datetime getDatetime() {
        return datetime.get();
    }

    public void setDatetime(Datetime datetime) {
        this.datetime.set(requireNonNull(datetime));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ReadOnlyEvent
                && this.isSameStateAs((ReadOnlyEvent) other));
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, location, datetime);
    }

    @Override
    public String toString() {
        return getAsText();
    }
}
```
###### \java\seedu\address\model\event\ReadOnlyEvent.java
``` java

/**
 * Interface for Events related to Calendar View feature.
 */
public interface ReadOnlyEvent {
    ObjectProperty<String> titleProperty();

    String getTitle();

    ObjectProperty<String> descriptionProperty();

    String getDescription();

    ObjectProperty<String> locationProperty();

    String getLocation();

    ObjectProperty<Datetime> datetimeProperty();

    Datetime getDatetime();

    /**
     * Checks if @param other is of the same state as this.
     */
    default boolean isSameStateAs(ReadOnlyEvent other) {
        return other == this // short circuit if same object
                || (other != null
                && other.getTitle().equals(this.getTitle())
                && other.getDescription().equals(this.getDescription())
                && other.getLocation().equals(this.getLocation())
                && other.getDatetime().equals(this.getDatetime()));
    }

    /**
     * Formats the person as text, showing all contact details.
     */
    default String getAsText() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getTitle())
                .append(" Description: ")
                .append(getDescription())
                .append(" Location: ")
                .append(getLocation())
                .append(" Datetime: ")
                .append(getDatetime());
        return builder.toString();
    }
}
```
###### \java\seedu\address\model\event\ReadOnlyEventBook.java
``` java

/**
 * A read-only immutable interface for a event in the eventbook.
 * Implementations should guarantee: details are present and not null, field values are validated.
 */
public interface ReadOnlyEventBook {

    ObjectProperty<String> titleProperty();

    String getTitle();

    ObjectProperty<String> descriptionProperty();

    String getDescription();

    ObjectProperty<String> locationProperty();

    String getLocation();

    ObjectProperty<String> datetimeProperty();

    String getDatetime();

    /**
     * Returns true if both have the same state. (interfaces cannot override .equals)
     */
    default boolean isSameStateAs(ReadOnlyEventBook other) {
        return other == this // short circuit if same object
                || (other != null // this is first to avoid NPE below
                && other.getTitle().equals(this.getTitle()) // state checks here onwards
                && other.getDescription().equals(this.getDescription())
                && other.getLocation().equals(this.getLocation())
                && other.getDatetime().equals(this.getDatetime()));
    }

    /**
     * Formats the person as text, showing all contact details.
     */
    default String getAsText() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getTitle())
                .append(" Description: ")
                .append(getDescription())
                .append(" Location: ")
                .append(getLocation())
                .append(" Datetime: ")
                .append(getDatetime());
        return builder.toString();
    }

    List<? extends ReadOnlyEvent> getEventList();
}
```
###### \java\seedu\address\model\event\TitleContainsKeywordsPredicate.java
``` java

/**
 * Tests that a {@code ReadOnlyEvent}'s {@code Title} matches any of the keywords given.
 */
public class TitleContainsKeywordsPredicate implements Predicate<ReadOnlyEvent> {
    private static String predicateType = "et";
    private final List<String> keywords;

    public TitleContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = keywords;
    }

    public static void setPredicateType(String predicateType) {
        TitleContainsKeywordsPredicate.predicateType = predicateType;
    }

    @Override
    public boolean test(ReadOnlyEvent event) {
        if (predicateType.equals("et")) {
            return keywords.stream()
                    .anyMatch(keyword -> StringUtil.containsWordIgnoreCase(event.getTitle(), keyword));
        }

        if (predicateType.equals("edt")) {
            return keywords.stream()
                    .anyMatch(keyword -> StringUtil.containsWordIgnoreCase(event.getDatetime().value, keyword));
        }

        if (predicateType.equals("ed")) {
            return keywords.stream()
                    .anyMatch(keyword -> StringUtil.containsWordIgnoreCase(event.getDescription(), keyword));
        }

        return false;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof TitleContainsKeywordsPredicate
                && this.keywords.equals(((TitleContainsKeywordsPredicate) other).keywords));
    }
}
```
###### \java\seedu\address\model\event\UniqueEventList.java
``` java

/**
 * A list of events that enforces uniqueness between its elements and does not allow nulls.
 *
 * Supports a minimal set of list operations.
 */
public class UniqueEventList implements Iterable<Event> {

    private final ObservableList<Event> internalList = FXCollections.observableArrayList();

    private final ObservableList<ReadOnlyEvent> mappedList = EasyBind.map(internalList, (event) -> event);

    /**
     * Returns true if the list contains an equivalent event as the given argument.
     */
    public boolean contains(ReadOnlyEvent toCheck) {
        requireNonNull(toCheck);
        return internalList.contains(toCheck);
    }

    /**
     * Adds an event to the list.
     */
    public void add(ReadOnlyEvent toAdd) throws CommandException {
        requireNonNull(toAdd);
        if (contains(toAdd)) {
            throw new CommandException("");
        }
        internalList.add(new Event(toAdd));
    }

    /**
     * Replaces the event {@code target} in the list with {@code editedEvent}.
     */
    public void setEvent(ReadOnlyEvent target, ReadOnlyEvent editedEvent)
            throws CommandException {
        requireNonNull(editedEvent);

        int index = internalList.indexOf(target);
        if (index == -1) {
            throw new CommandException("");
        }

        if (!target.equals(editedEvent) && internalList.contains(editedEvent)) {
            throw new CommandException("");
        }

        internalList.set(index, new Event(editedEvent));
    }

    /**
     * Removes the equivalent event from the list.
     *
     * @throws CommandException if no such event could be found in the list.
     */
    public boolean remove(ReadOnlyEvent toRemove) throws CommandException {
        requireNonNull(toRemove);
        final boolean eventFoundAndDeleted = internalList.remove(toRemove);
        if (!eventFoundAndDeleted) {
            throw new CommandException("");
        }
        return eventFoundAndDeleted;
    }

    public void setEvents(UniqueEventList replacement) {
        this.internalList.setAll(replacement.internalList);
    }

    public void setEvents(List<? extends ReadOnlyEvent> events) throws CommandException {
        final UniqueEventList replacement = new UniqueEventList();
        for (final ReadOnlyEvent event : events) {
            replacement.add(new Event(event));
        }
        setEvents(replacement);
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<ReadOnlyEvent> asObservableList() {
        return FXCollections.unmodifiableObservableList(mappedList);
    }

    /**
     * Orders the list.
     */
    public void orderBy(String parameter) throws CommandException {
        requireNonNull(parameter);
        Comparator<Event> orderByTitle = (Event a, Event b) -> a.getTitle().toString()
                .compareToIgnoreCase(b.getTitle().toString());
        Comparator<Event> orderByLocation = (Event a, Event b) -> a.getLocation().toString()
                .compareToIgnoreCase(b.getLocation().toString());
        Comparator<Event> orderByDatetime = (Event a, Event b) -> {

            SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy hhmm");
            try {
                Date datetime1 = formatDate.parse(a.getDatetime().value);
                Date datetime2 = formatDate.parse(b.getDatetime().value);

                return datetime2.compareTo(datetime1);

            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;

        };

        switch (parameter) {
        case "TITLE":
            internalList.sort(orderByTitle);
            break;

        case "LOCATION":
            internalList.sort(orderByLocation);
            break;

        case "DATETIME":
            internalList.sort(orderByDatetime);
            break;

        default:
            throw new CommandException("");
        }


    }

    @Override
    public Iterator<Event> iterator() {
        return internalList.iterator();
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof UniqueEventList
                && this.internalList.equals(((UniqueEventList) other).internalList));
    }
}
```
###### \java\seedu\address\model\EventBook.java
``` java

/**
 * Wraps all data at the event-book level
 * Duplicates are not allowed (by .equals comparison)
 */
public class EventBook implements ReadOnlyEventBook {

    private final UniqueEventList events;

    /*
     * The 'unusual' code block below is an non-static initialization block, sometimes used to avoid duplication
     * between constructors. See https://docs.oracle.com/javase/tutorial/java/javaOO/initial.html
     *
     * Note that non-static init blocks are not recommended to use. There are other ways to avoid duplication
     *   among constructors.
     */
    {
        events = new UniqueEventList();
    }

    public EventBook() {
    }

    /**
     * Creates an EventBook using the Events in the {@code toBeCopied}
     */
    public EventBook(ReadOnlyEventBook toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    public void setEvents(List<? extends ReadOnlyEvent> events) throws CommandException {
        this.events.setEvents(events);
    }

    /**
     * Resets the existing data of this {@code EventBook} with {@code newData}.
     */
    public void resetData(ReadOnlyEventBook newData) {
        requireNonNull(newData);
        try {
            setEvents(newData.getEventList());
        } catch (CommandException e) {
            assert false : "EventBooks should not have duplicate events";
        }
    }

    /**
     * Adds an event to the event book.
     *
     * @throws CommandException if an equivalent event already exists.
     */
    public void addEvent(ReadOnlyEvent e) throws CommandException {
        Event newEvent = new Event(e);
        events.add(newEvent);
    }

    /**
     * Replaces the given event {@code target} in the list with {@code editedReadOnlyEvent}.
     *
     */
    public void updateEvent(ReadOnlyEvent target, ReadOnlyEvent editedReadOnlyEvent)
            throws CommandException {
        requireNonNull(editedReadOnlyEvent);

        Event editedPerson = new Event(editedReadOnlyEvent);
        events.setEvent(target, editedPerson);
    }

    /**
     * Removes {@code key} from this {@code EventBook}.
     */
    public boolean removeEvent(ReadOnlyEvent key) throws CommandException {
        if (events.remove(key)) {
            return true;
        } else {
            throw new CommandException("");
        }
    }

    /**
     * Order list of all events in the event Book based on the parameter.
     */
    public void orderList(String parameter) throws CommandException {
        events.orderBy(parameter);
    }

    //// util methods

    @Override
    public int hashCode() {
        return Objects.hash(events);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof EventBook // instanceof handles nulls
                && this.events.equals(((EventBook) other).events));
    }

    @Override
    public String toString() {
        return events.asObservableList().size() + " events";
    }

    @Override
    public ObjectProperty<String> titleProperty() {
        return null;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public ObjectProperty<String> descriptionProperty() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public ObjectProperty<String> locationProperty() {
        return null;
    }

    @Override
    public String getLocation() {
        return null;
    }

    @Override
    public ObjectProperty<String> datetimeProperty() {
        return null;
    }

    @Override
    public String getDatetime() {
        return null;
    }

    @Override
    public ObservableList<ReadOnlyEvent> getEventList() {
        return events.asObservableList();
    }
}
```
###### \java\seedu\address\storage\CsvFileStorage.java
``` java

/**
 * Stores addressbook data in a CSV file
 */
public class CsvFileStorage {

    // This string forms the header for the Google CSV format.
    public static final String GOOGLE_CSV_FORMAT = "Name,Given Name,Additional Name,Family Name,Yomi Name,"
            + "Given Name Yomi,Additional Name Yomi,Family Name Yomi,Name Prefix,Name Suffix,Initials,Nickname,"
            + "Short Name,Maiden Name,Birthday,Gender,Location,Billing Information,Directory Server,Mileage,"
            + "Occupation,Hobby,Sensitivity,Priority,Subject,Notes,Group Membership,E-mail 1 - Type,E-mail 1 - Value,"
            + "Phone 1 - Type,Phone 1 - Value,Phone 2 - Type,Phone 2 - Value,Organization 1 - Type,"
            + "Organization 1 - Name,Organization 1 - Yomi Name,Organization 1 - Title,Organization 1 - Department,"
            + "Organization 1 - Symbol,Organization 1 - Location,Organization 1 - Job Description";

    /**
     * Saves the given addressbook data to the specified file.
     */
    public static void saveDataToFile(ReadOnlyAddressBook addressBook, String filePath) throws IOException {

        PrintWriter pw = new PrintWriter(new File(filePath));
        StringBuilder sb = new StringBuilder();

        sb.append(GOOGLE_CSV_FORMAT);
        sb.append('\n');
        for (Person person : addressBook.getPersonList()) {
            sb.append(person.getName());
            sb.append(",,,,,,,,,,,,,,,,,,,,,,,,,,");
            sb.append("* My Contacts");
            sb.append(",,");
            sb.append(person.getEmail());
            sb.append(",,");
            sb.append(person.getPhone());
            sb.append(",,,,,,,,,,");
            sb.append('\n');
        }
        pw.write(sb.toString());
        pw.close();
    }

}
```
###### \java\seedu\address\storage\EventBookStorage.java
``` java

/**
 * Represents a storage for {@link seedu.address.model.EventBook}.
 */
public interface EventBookStorage {

    /**
     * Returns the file path of the data file.
     */
    String getEventBookFilePath();

    /**
     * Returns AddressBook data as a {@link ReadOnlyEventBook}.
     * Returns {@code Optional.empty()} if storage file is not found.
     *
     * @throws DataConversionException if the data in storage is not in the expected format.
     * @throws IOException             if there was any problem when reading from the storage.
     */
    Optional<ReadOnlyEventBook> readEventBook() throws DataConversionException, IOException, JAXBException;

    /**
     * @see #getEventBookFilePath()
     */
    Optional<ReadOnlyEventBook> readEventBook(String filePath)
            throws DataConversionException, IOException, JAXBException;

    /**
     * Saves the given {@link ReadOnlyEventBook} to the storage.
     *
     * @param eventBook cannot be null.
     * @throws IOException if there was any problem writing to the file.
     */
    void saveEventBook(ReadOnlyEventBook eventBook) throws IOException;

    /**
     * @see #saveEventBook(ReadOnlyEventBook)
     */
    void saveEventBook(ReadOnlyEventBook eventBook, String filePath) throws IOException;

    /**
     * @see #saveEventBook(ReadOnlyEventBook)
     */
    void backupEventBook(ReadOnlyEventBook eventBook) throws IOException;

    /**
     * @see #exportEventBook()
     */
    void exportEventBook() throws ParserConfigurationException, IOException, TransformerException;

}
```
###### \java\seedu\address\storage\XmlEventBookStorage.java
``` java

/**
 * A class to access TunedIn EventBook data stored as an xml file on the hard disk.
 */
public class XmlEventBookStorage implements EventBookStorage {

    private static final Logger logger = LogsCenter.getLogger(XmlEventBookStorage.class);

    private String filePath;
    private String exportedPath;
    private String header;


    public XmlEventBookStorage(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String getEventBookFilePath() {
        return filePath;
    }

    @Override
    public Optional<ReadOnlyEventBook> readEventBook() throws IOException, JAXBException, DataConversionException {
        return readEventBook(filePath);
    }

    @Override
    public Optional<ReadOnlyEventBook> readEventBook(String filePath) throws DataConversionException,
            FileNotFoundException, JAXBException {
        requireNonNull(filePath);

        File eventBookFile = new File(filePath);

        if (!eventBookFile.exists()) {
            logger.info("EventBook file " + eventBookFile + " not found");
            return Optional.empty();
        }

        ReadOnlyEventBook eventBookOptional = XmlFileStorage.loadEventDataFromSaveFile(new File(filePath));

        return Optional.of(eventBookOptional);
    }

    @Override
    public void saveEventBook(ReadOnlyEventBook eventBook) throws IOException {
        saveEventBook(eventBook, filePath);
    }

    @Override
    public void saveEventBook(ReadOnlyEventBook eventBook, String filePath) throws IOException {
        requireNonNull(eventBook);
        requireNonNull(filePath);

        File file = new File(filePath);
        FileUtil.createIfMissing(file);
        XmlFileStorage.saveDataToFile(file, new XmlSerializableEventBook(eventBook));
    }

    @Override
    public void backupEventBook(ReadOnlyEventBook eventBook) throws IOException {
        saveEventBook(eventBook, filePath + "-backup");
    }

    @Override
    public void exportEventBook() throws ParserConfigurationException, IOException {
        //TODO
    }
}
```
###### \java\seedu\address\ui\CalendarView.java
``` java

/**
 * WORK IN PROGRESS FOR EVENTS AND LOGGING
 */
public class CalendarView {
    private ArrayList<AnchorPaneNode> calendarMonth = new ArrayList<>(35);
    private VBox view;
    private Text calendarTitle;
    private YearMonth currentYearMonth;
    private ObservableList<ReadOnlyEvent> eventList;
    private Logic logic;
    private final Logger logger = LogsCenter.getLogger(CommandBox.class);

    /**
     * Provides layout for the calendar month with anchor panes.
     */
    public CalendarView(Logic logic, ObservableList<ReadOnlyEvent> eventList, YearMonth yearMonth) {
        this.logic = logic;
        this.eventList = eventList;
        currentYearMonth = yearMonth;

        // Creates the calendar grid pane
        GridPane calendar = new GridPane();
        calendar.setPrefSize(450, 300);

        // Create rows and columns of anchor panes for calendar
        calendarMonthSetup(calendar);

        // Days of the Week
        Text[] days = new Text[]{new Text("SUNDAY"), new Text("MONDAY"), new Text("TUESDAY"),
            new Text("WEDNESDAY"), new Text("THURSDAY"), new Text("FRIDAY"), new Text("SATURDAY")};
        GridPane dayLabels = new GridPane();
        dayLabels.setPrefWidth(450);
        Integer col = 0;

        for (Text day : days) {
            day.setFill(Color.WHITE);
            day.setFont(new Font("Serif", 13));
            AnchorPane ap = new AnchorPane();
            ap.setPrefSize(200, 10);
            AnchorPane.setBottomAnchor(day, 5.0);
            ap.getChildren().add(day);
            dayLabels.add(ap, col++, 0);
        }

        // Creates a title for the calendar
        calendarTitle = new Text();
        calendarTitle.setFill(Color.WHITE);

        // Buttons to navigate through months
        Button previousMonth = new Button("< Previous");
        previousMonth.setOnAction(e -> previousMonth());
        Button nextMonth = new Button("Next >");
        nextMonth.setOnAction(e -> nextMonth());
        HBox titleBar = new HBox(previousMonth, calendarTitle, nextMonth);
        HBox.setMargin(calendarTitle, new Insets(0, 20, 0, 20));
        titleBar.setAlignment(Pos.BASELINE_CENTER);

        // Populate calendar with the appropriate day numbers
        populateCalendar(yearMonth, null);

        // Creates the calendar view
        view = new VBox(titleBar, dayLabels, calendar);
        VBox.setMargin(titleBar, new Insets(0, 0, 15, 0));

    }

    /**
     * WORK IN PROGRESS FOR EVENTS
     */
    public void populateCalendar(YearMonth yearMonth, Index targetIndex) {
        // Gets the current date as reference
        LocalDate calendarDate = LocalDate.of(yearMonth.getYear(), yearMonth.getMonthValue(), 1);

        // Sets first day to be a Sunday
        while (!calendarDate.getDayOfWeek().toString().equals("SUNDAY")) {
            calendarDate = calendarDate.minusDays(1);
        }

        // Fills up calendar with day numbers
        for (AnchorPaneNode ap : calendarMonth) {
            if (ap.getChildren().size() != 0) {
                ap.getChildren().remove(0);
            }

            String dayValue = String.valueOf(calendarDate.getDayOfMonth());
            String monthValue = String.valueOf(calendarDate.getMonthValue());
            String yearValue = String.valueOf(calendarDate.getYear());

            boolean eventExist = false;

            if (targetIndex == null) {
                eventExist = eventList.stream()
                        .anyMatch(e -> checkEventDay(e, dayValue)
                                && checkEventMonth(e, monthValue)
                                && checkEventYear(e, yearValue));
            } else {
                ReadOnlyEvent e = eventList.get(targetIndex.getZeroBased());

                if (checkEventDay(e, dayValue)
                        && checkEventMonth(e, monthValue)
                        && checkEventYear(e, yearValue)) {
                    eventExist = true;
                }
            }

            Text dateNumber = new Text(String.valueOf(calendarDate.getDayOfMonth()));
            // Days from a different month shows up as a different colour
            if (calendarDate.getMonthValue() != yearMonth.getMonthValue()) {
                dateNumber.setFill(Color.DARKGREY);
            } else {
                dateNumber.setFill(Color.WHITE);
            }
            ap.setDate(calendarDate);
            ap.setTopAnchor(dateNumber, 5.0);
            ap.setLeftAnchor(dateNumber, 5.0);

            if (eventExist) {
                ap.setOnMouseClicked(ev -> {
                    String commandText = FindEventCommand.getCommandWord()
                            + " " + PREFIX_EVENT_DATETIME + getFormatDate(dayValue, monthValue, yearValue);
                    try {
                        CommandResult commandResult = logic.execute(commandText);
                        logger.info("Command Result: " + commandResult.feedbackToUser);

                    } catch (CommandException | IllegalValueException e) {
                        logger.info("Invalid Command: " + commandText);
                    }
                });
                ap.setStyle("-fx-background-color: #2e5577;");
            } else {
                ap.setStyle("-fx-background-color: #3d719d;");
            }

            ap.getChildren().add(dateNumber);
            calendarDate = calendarDate.plusDays(1);
        }

        // Change the title of the calendar
        calendarTitle.setText(yearMonth.getMonth().toString() + " " + String.valueOf(yearMonth.getYear()));
    }

    /**
     * Provides layout for the calendar month with anchor panes.
     */
    private void calendarMonthSetup(GridPane calendar) {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 7; j++) {
                AnchorPaneNode ap = new AnchorPaneNode();
                ap.getStyleClass().add("anchor");
                ap.setPrefSize(100, 100);
                calendar.add(ap, j, i);
                calendarMonth.add(ap);
            }
        }
    }

    private void previousMonth() {
        currentYearMonth = currentYearMonth.minusMonths(1);
        populateCalendar(currentYearMonth, null);
    }

    private void nextMonth() {
        currentYearMonth = currentYearMonth.plusMonths(1);
        populateCalendar(currentYearMonth, null);
    }

    public YearMonth getCurrentYearMonth() {
        return currentYearMonth;
    }

    public void setCurrentYearMonth(YearMonth currentYearMonth) {
        this.currentYearMonth = currentYearMonth;
    }

    public VBox getView() {
        return view;
    }

    public ArrayList<AnchorPaneNode> getAllCalendarDays() {
        return calendarMonth;
    }

    public void setAllCalendarDays(ArrayList<AnchorPaneNode> allCalendarDays) {
        this.calendarMonth = allCalendarDays;
    }

    /**
     * Check whether the event Day matches the input dayValue
     * @param event
     * @param dayValue
     * @return
     */
    private boolean checkEventDay(ReadOnlyEvent event, String dayValue) {
        if (dayValue.length() == 1) {
            return event.getDatetime().value.substring(0, 2).equals("0" + dayValue);
        } else {
            return event.getDatetime().value.substring(0, 2).equals(dayValue);
        }
    }

    /**
     * Check whether the event Day matches the input monthValue
     * @param event
     * @param monthValue
     * @return
     */
    private boolean checkEventMonth(ReadOnlyEvent event, String monthValue) {
        if (monthValue.length() == 1) {
            return event.getDatetime().value.substring(3, 5).equals("0" + monthValue);
        } else {
            return event.getDatetime().value.substring(3, 5).equals(monthValue);
        }
    }

    /**
     * Check whether the event Day matches the input yearValue
     * @param event
     * @param yearValue
     * @return
     */
    private boolean checkEventYear(ReadOnlyEvent event, String yearValue) {
        return event.getDatetime().value.substring(6, 10).equals(yearValue);
    }

    private String getFormatDate(String day, String month, String year) {
        if (day.length() == 1) {
            day = "0" + day;
        }
        if (month.length() == 1) {
            month = "0" + month;
        }
        return day + "-" + month + "-" + year;
    }

}
```
###### \java\seedu\address\ui\CalendarViewPanel.java
``` java

/**
 * Panel containing the calendar.
 */
public class CalendarViewPanel extends UiPart<Region> {
    private static final String FXML = "CalendarView.fxml";

    @FXML
    private Pane calendarPanel;

    private CalendarView calendarView;
    private Logic logic;

    public CalendarViewPanel(Logic logic) {
        super(FXML);
        this.logic = logic;
        setConnections();
    }

    private void setConnections() {
        calendarView = new CalendarView(logic, logic.getFilteredEventList(), YearMonth.now());
        calendarPanel.getChildren().add(calendarView.getView());
    }

    public CalendarView getCalendarPane() {
        return calendarView;
    }
}
```
###### \java\seedu\address\ui\CalendarViewUpdate.java
``` java

/**
 * Methods to update Calendar View
 */
public class CalendarViewUpdate {

    /**
     * Updates view state of Calendar UI
     * for methods like add, delete, edit, etc.
     *
     * @param calendarView
     */
    public static void updateViewState(CalendarView calendarView) {
        calendarView.setCurrentYearMonth(YearMonth.now());
        calendarView.populateCalendar(calendarView.getCurrentYearMonth(), null);
    }

    /**
     * Updates view state of Calendar UI
     * for the Find Event Command.
     *
     * @param calendarView
     * @param model
     */
    public static void updateFindState(CalendarView calendarView, Model model) {
        List<ReadOnlyEvent> events = model.getFilteredEventList();
        if (events.size() != 0) {
            String findYearMonth = events.get(0).getDatetime().value.substring(3, 10);
            // If every event in the filtered list is on the same day, Calendar View jumps to that day.
            boolean changeSelectedYearMonth = events.stream()
                    .allMatch(e -> e.getDatetime().value.substring(3, 10).equals(findYearMonth));

            if (changeSelectedYearMonth) {
                calendarView.setCurrentYearMonth(YearMonth.parse(findYearMonth,
                        DateTimeFormatter.ofPattern("MM-yyyy")));
                calendarView.populateCalendar(calendarView.getCurrentYearMonth(), null);
            }
        }
    }
}
```
###### \java\seedu\address\ui\EventCard.java
``` java

/**
 * An UI component that displays information of a {@code Event}.
 */
public class EventCard extends UiPart<Region> {

    private static final String FXML = "EventListCard.fxml";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final ReadOnlyEvent event;

    @FXML
    private HBox cardPane;
    @FXML
    private Label title;
    @FXML
    private Label id;
    @FXML
    private Label description;
    @FXML
    private Label eventLocation;
    @FXML
    private Label datetime;

    public EventCard(ReadOnlyEvent event, int displayedIndex) {
        super(FXML);
        id.setText(displayedIndex + ". ");
        this.event = event;
        bindListeners(event);
    }

    /**
     * Binds the individual UI elements to observe their respective {@code Event} properties
     * so that they will be notified of any changes.
     */
    private void bindListeners(ReadOnlyEvent event) {
        title.textProperty().bind(Bindings.convert(event.titleProperty()));
        description.textProperty().bind(Bindings.convert(event.descriptionProperty()));
        eventLocation.textProperty().bind(Bindings.convert(event.locationProperty()));
        datetime.textProperty().bind(Bindings.convert(event.datetimeProperty()));
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EventCard)) {
            return false;
        }

        // state check
        EventCard card = (EventCard) other;
        return id.getText().equals(card.id.getText())
                && event.equals(card.event);
    }
}
```
###### \java\seedu\address\ui\EventListPanel.java
``` java

/**
 * Panel containing the list of events.
 */
public class EventListPanel extends UiPart<Region> {
    private static final String FXML = "EventListPanel.fxml";
    private final Logger logger = LogsCenter.getLogger(EventListPanel.class);

    @FXML
    private ListView<EventCard> eventListView;

    public EventListPanel(ObservableList<ReadOnlyEvent> eventList) {
        super(FXML);
        setConnections(eventList);
        registerAsAnEventHandler(this);
    }

    private void setConnections(ObservableList<ReadOnlyEvent> eventList) {
        ObservableList<EventCard> mappedList = EasyBind.map(
                eventList, (event) -> new EventCard(event, eventList.indexOf(event) + 1));
        eventListView.setItems(mappedList);
        eventListView.setCellFactory(listView -> new EventListViewCell());
        setEventHandlerForSelectionChangeEvent();
    }

    private void setEventHandlerForSelectionChangeEvent() {
        eventListView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        logger.fine("Selection in person list panel changed to : '" + newValue + "'");
                        raise(new EventPanelSelectionChangedEvent(newValue));
                    }
                });
    }

    /**
     * Scrolls to the {@code PersonCard} at the {@code index} and selects it.
     */
    private void scrollTo(int index) {
        Platform.runLater(() -> {
            eventListView.scrollTo(index);
            eventListView.getSelectionModel().clearAndSelect(index);
        });
    }

    @Subscribe
    private void handleJumpToListRequestEvent(JumpToListRequestEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        scrollTo(event.targetIndex);
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code EventCard}.
     */
    class EventListViewCell extends ListCell<EventCard> {

        @Override
        protected void updateItem(EventCard event, boolean empty) {
            super.updateItem(event, empty);

            if (empty || event == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(event.getRoot());
            }
        }
    }
}
```
###### \java\seedu\address\ui\StackOverflowWindow.java
``` java

/**
 * Controller for a stackoverflow page
 */
public class StackOverflowWindow extends UiPart<Stage> {

    public static final String STACKOVERFLOW_URL = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";

    private static final Logger logger = LogsCenter.getLogger(StackOverflowWindow.class);
    private static final String FXML = "StackOverflowWindow.fxml";

    @FXML
    private WebView browser;

    /**
     * Creates a new StackOverflowWindow.
     *
     * @param root Stage to use as the root of the StackOverflowWindow.
     */
    public StackOverflowWindow(Stage root) {
        super(FXML, root);
        browser.getEngine().load(STACKOVERFLOW_URL);
    }

    /**
     * Creates a new StackOverflowWindow.
     */
    public StackOverflowWindow() {
        this(new Stage());
    }

    /**
     * Shows the stackoverflow window.
     * @throws IllegalStateException
     * <ul>
     *     <li>
     *         if this method is called on a thread other than the JavaFX Application Thread.
     *     </li>
     *     <li>
     *         if this method is called during animation or layout processing.
     *     </li>
     *     <li>
     *         if this method is called on the primary stage.
     *     </li>
     *     <li>
     *         if {@code dialogStage} is already showing.
     *     </li>
     * </ul>
     */
    public void show() {
        logger.fine("Showing the stackoverflow page.");
        getRoot().show();
    }
}
```
###### \resources\view\MainWindow.fxml
``` fxml
          <VBox fx:id="eventTaskView" minWidth="380" prefWidth="380" SplitPane.resizableWithParent="false">
            <padding>
              <Insets top="10" right="10" bottom="10" left="10"/>
            </padding>
            <TabPane fx:id="tabPane" VBox.vgrow="ALWAYS" tabClosingPolicy="UNAVAILABLE">
              <tabs>
                <Tab fx:id="eventTab" text="Events">
                  <StackPane fx:id="eventListPanelPlaceholder" VBox.vgrow="ALWAYS"/>
                </Tab>
                <Tab fx:id="taskTab" text="Tasks">
                  <StackPane fx:id="taskListPanelPlaceholder" VBox.vgrow="ALWAYS"/>
                </Tab>
              </tabs>
            </TabPane>
          </VBox>
        </SplitPane>

        <StackPane fx:id="statusbarPlaceholder" VBox.vgrow="NEVER" />
      </VBox>
    </Scene>
  </scene>
</fx:root>
```
