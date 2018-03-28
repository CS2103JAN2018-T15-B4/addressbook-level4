package seedu.address.model.person;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.io.File;
import java.nio.file.Files;
import javax.activation.MimetypesFileTypeMap;

/**
 * Represents a Picture in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Picture {

    public static final String DEFAULT_PATH = "images/default.png";
    public static final String MESSAGE_PICTURE_CONSTRAINTS =
            "Filepath must be valid, and point to an image file"; //size
    public static final String PICTURE_VALIDATION_REGEX = "[^\\s].*";
    public static final String APPDATA_DIR = defaultDirectory();
    public static final String FOLDER = APPDATA_DIR + "/AddressBook";
    public static final String MESSAGE_USAGE = "dummy";
    private static final String URL_PREFIX = "file:/";

    private String path;

    /**
     * Default initializer, uses default picture
     */
    public Picture() {
        //APPDATA_DIR = defaultDirectory();
        this.path = DEFAULT_PATH;
    }

    /**
     * initializer if path pointing to pic is specified
     *
     * @param path
     */
    public Picture(String path) {
        this.path = path;
    }

    /**
     * initializer if path, and image name of new picture is specified
     *
     * @param path
     * @param newPictureName
     */
    public Picture(String path, String newPictureName) {

        requireNonNull(path);
        checkArgument(isValidPath(path), MESSAGE_PICTURE_CONSTRAINTS);
        String extension = "";

        int i = path.lastIndexOf('.');
        if (i > 0) {
            extension = path.substring(i + 1);
        }
        this.path = "ProfilePicture/" + newPictureName + "." + extension;

        createNewPicture(path, newPictureName + "." + extension);

    }

    /**
     * Check if path is valid
     *
     * @param path
     * @return
     */
    public boolean isValidPath(String path) {

        if (!path.matches(PICTURE_VALIDATION_REGEX)) {
            return false;
        }
        return isValidPicture(path);
    }

    /**
     * Checks if the image pointed to by the path is indeed a valid image
     *
     * @param path
     * @return
     */
    public static boolean isValidPicture(String path) {

        File f = new File(path);
        String mimetype = new MimetypesFileTypeMap().getContentType(f);
        String type = mimetype.split("/")[0];
        return type.equals("image");
    }

    /**
     * copies the file from (@code source) to the profile pic folder
     *
     * @param source
     * @param dstFilename
     */
    public void createNewPicture(String source, String dstFilename) {

        File folder = new File(FOLDER);

        if (!folder.exists()) {

            if (!folder.mkdir()) {
                System.out.println("FAILED TO MAKE FOLDER");
            }
        }
        File src = null;

        try {
            src = new File(source);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
        File dest = new File(FOLDER + "//" + dstFilename);
        System.out.println(dest.getPath());
        if (!dest.exists()) {
            try {
                dest.createNewFile();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        
        try {
            Files.copy(src.toPath(), dest.toPath() , REPLACE_EXISTING);
            this.path = URL_PREFIX + dest.toPath().toString();
        } catch (Exception e) {
            System.out.println("ERROR COPYING: " + e.getMessage());
        }
    }

    public String getPath() {
        return this.path;
    }

    /**
     * Determines the User OS and output the appropriate folder to store profile pic
     *
     * @return
     */
    private static String defaultDirectory() {
        String os = System.getProperty("os.name").toUpperCase();
        if (os.contains("WIN")) {
            return System.getenv("APPDATA");
        } else if (os.contains("MAC")) {
            return System.getProperty("user.home") + "/Library/Application "
                    + "Support";
        } else if (os.contains("NUX")) {
            return System.getProperty("user.home");
        }
        return System.getProperty("user.dir");
    }


}
