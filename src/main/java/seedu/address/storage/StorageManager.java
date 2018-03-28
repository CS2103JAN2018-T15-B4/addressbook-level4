package seedu.address.storage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import com.google.common.eventbus.Subscribe;

import org.xml.sax.SAXException;
import seedu.address.commons.core.ComponentManager;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.events.model.AddressBookChangedEvent;
import seedu.address.commons.events.model.EventBookChangedEvent;
import seedu.address.commons.events.storage.DataSavingExceptionEvent;
import seedu.address.commons.exceptions.DataConversionException;
import seedu.address.commons.exceptions.ExistingFileException;
import seedu.address.commons.exceptions.InvalidFileException;
import seedu.address.model.EventBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.event.Event;
import seedu.address.model.event.ReadOnlyEventBook;

/**
 * Manages storage of AddressBook data in local storage.
 */
public class StorageManager extends ComponentManager implements Storage {

    private static final Logger logger = LogsCenter.getLogger(StorageManager.class);
    private AddressBookStorage addressBookStorage;
    private EventBookStorage eventBookStorage;
    private UserPrefsStorage userPrefsStorage;


    public StorageManager(AddressBookStorage addressBookStorage, EventBookStorage eventBookStorage, UserPrefsStorage userPrefsStorage) {
        super();
        this.addressBookStorage = addressBookStorage;
        this.eventBookStorage = eventBookStorage;
        this.userPrefsStorage = userPrefsStorage;
    }

    // ================ UserPrefs methods ==============================

    @Override
    public String getUserPrefsFilePath() {
        return userPrefsStorage.getUserPrefsFilePath();
    }

    @Override
    public Optional<UserPrefs> readUserPrefs() throws DataConversionException, IOException {
        return userPrefsStorage.readUserPrefs();
    }

    @Override
    public void saveUserPrefs(UserPrefs userPrefs) throws IOException {
        userPrefsStorage.saveUserPrefs(userPrefs);
    }


    // ================ AddressBook methods ==============================

    @Override
    public String getAddressBookFilePath() {
        return addressBookStorage.getAddressBookFilePath();
    }

    @Override
    public Optional<ReadOnlyAddressBook> readAddressBook() throws DataConversionException, IOException {
        return readAddressBook(addressBookStorage.getAddressBookFilePath());
    }

    @Override
    public Optional<ReadOnlyAddressBook> readAddressBook(String filePath) throws DataConversionException, IOException {
        logger.fine("Attempting to read data from file: " + filePath);
        try {
            return addressBookStorage.readAddressBook(filePath);
        } catch (JAXBException e) {
            throw new AssertionError("JAXBException");
        }
    }

    @Override
    public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
        saveAddressBook(addressBook, addressBookStorage.getAddressBookFilePath());
    }

    @Override
    public void saveAddressBook(ReadOnlyAddressBook addressBook, String filePath)
            throws IOException {
        logger.fine("Attempting to write to data file: " + filePath);
        addressBookStorage.saveAddressBook(addressBook, filePath);
    }

    @Override
    public void exportAddressBook(ReadOnlyAddressBook addressBook)
            throws IOException, InvalidFileException, ExistingFileException {
        exportAddressBook(addressBook, addressBookStorage.getAddressBookFilePath());
    }

    @Override
    public void exportAddressBook(ReadOnlyAddressBook addressBook, String filePath)
            throws IOException, InvalidFileException, ExistingFileException {
        addressBookStorage.exportAddressBook(addressBook, filePath);
    }

    @Override
    public void backupAddressBook(ReadOnlyAddressBook addressBook) throws IOException, InvalidFileException {
        addressBookStorage.backupAddressBook(addressBook);
    }


    @Override
    @Subscribe
    public void handleAddressBookChangedEvent(AddressBookChangedEvent event) throws InvalidFileException {
        logger.info(LogsCenter.getEventHandlingLogMessage(event, "Local data changed, saving to file"));
        try {
            saveAddressBook(event.data);
        } catch (IOException e) {
            raise(new DataSavingExceptionEvent(e));
        }

    }

    // ================ EventBook methods ==============================

    @Override
    public String getEventBookFilePath() {
        return eventBookStorage.getEventBookFilePath();
    }

    @Override
    public Optional<ReadOnlyEventBook> readEventBook() throws DataConversionException, IOException {
        return readEventBook(eventBookStorage.getEventBookFilePath());
    }

    @Override
    public Optional<ReadOnlyEventBook> readEventBook(String filePath) throws DataConversionException, IOException {
        logger.fine("Attempting to read data from file: " + filePath);
        try {
            return eventBookStorage.readEventBook(filePath);
        } catch (JAXBException e) {
            throw new AssertionError("Not supposed to have errors!");
        }
    }

    @Override
    public void exportEventBook() throws FileNotFoundException, ParserConfigurationException, IOException {
        try {
            eventBookStorage.exportEventBook();
        } catch (SAXException | TransformerException e) {
            throw new AssertionError("Not supposed to have errors!");
        }
    }

    @Override
    public void saveEventBook(ReadOnlyEventBook eventBook) throws IOException {
        saveEventBook(eventBook, eventBookStorage.getEventBookFilePath());
    }

    @Override
    public void saveEventBook(ReadOnlyEventBook eventBook, String filePath) throws IOException {
        logger.fine("Attempting to write to data file: " + filePath);
        eventBookStorage.saveEventBook(eventBook, filePath);
    }

    @Override
    public void backupEventBook(ReadOnlyEventBook eventBook) throws IOException {
        String filePath = getEventBookFilePath().substring(0, getEventBookFilePath().indexOf('.')) + "_backup.xml";
        eventBookStorage.saveEventBook(eventBook, filePath);
    }

    @Override
    @Subscribe
    public void handleEventBookChangedEvent(EventBookChangedEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event, "Local data changed, saving to file"));
        try {
            saveEventBook(event.data);
        } catch (IOException e) {
            raise(new DataSavingExceptionEvent(e));
        }
    }
}
