package mm.tstring.config;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.converters.FileConverter;
import mm.tstring.util.TStringGlobals;

import java.io.File;
import java.util.logging.Logger;

/**
 * @author m!m
 */
public class TStringConfig {
    private static final Logger logger =
            Logger.getLogger(TStringConfig.class
                    .getName());
    private static TStringConfig INSTANCE = TStringConfig.getInstance();
    private File backupDir = new File("backup").getAbsoluteFile();
    @Parameter(names = {"-h", "-help", "-?"},
            description = "Prints this help message")
    private boolean help = false;
    @Parameter(
            names = {"-m", "-mode"},
            converter = ModeConverter.class,
            description = "The mode in which this program will run. Defaults to 'create'.\n"
                    + "'create' to create the tstring.tbl\n"
                    + "'update' to update the tstring.tbl")
    private Mode mode = Mode.CREATE;
    @Parameter(
            names = "-nobackup",
            description = "Forces the program to not backup files that are beeing modified.")
    private boolean noBackup = false;
    @Parameter(
            names = {"-r", "-root"},
            converter = FileConverter.class,
            description = "The directory from which this programm will search in data/mission and data/tables")
    private File rootDir = new File(".").getAbsoluteFile();

    /**
     * Returns the dir in which the backup will be stored
     *
     * @return The backup directory
     */
    public static File getBackupDir() {
        return TStringConfig.getInstance().backupDir;
    }

    /**
     * Sets a new backupdirectory
     *
     * @param backupDir The new directory. If it doesn't exist the directory will be
     *                  created.
     */
    public static void setBackupDir(File backupDir) {
        if (backupDir == null) {
            throw new NullPointerException();
        }

        if (!backupDir.exists()) {
            if (!backupDir.mkdirs()) {
                TStringConfig.logger.severe("Couldn't create directory '"
                        + backupDir.getAbsolutePath() + "'!");
            }
        }
        TStringConfig.getInstance().backupDir = backupDir;
    }

    private static TStringConfig getInstance() {
        if (TStringConfig.INSTANCE == null) {
            return new TStringConfig();
        } else {
            return TStringConfig.INSTANCE;
        }
    }

    /**
     * Gives the mode in which the generator will run.
     *
     * @return The Mode
     */
    public static Mode getMode() {
        return TStringConfig.getInstance().mode;
    }

    /**
     * Sets the new {@link Mode}. Only effectiv when used before or in
     * {@link #initialize(String[])}
     *
     * @param mode The new Mode
     * @see Mode
     */
    public static void setMode(Mode mode) {
        TStringConfig.getInstance().mode = mode;
    }

    /**
     * This is the root Directory from where the missions and tables will be
     * searched
     *
     * @return The root directory
     */
    public static File getRootDir() {
        return TStringConfig.getInstance().rootDir;
    }

    /**
     * Sets the new root directory. If it doesn't exist it will be created.
     *
     * @param rootDir The new directory.
     */
    public static void setRootDir(File rootDir) {
        if (rootDir == null) {
            throw new NullPointerException();
        }

        if (!rootDir.exists()) {
            if (!rootDir.mkdirs()) {
                TStringConfig.logger.severe("Couldn't create directory '"
                        + rootDir.getAbsolutePath() + "'!");
                return;
            }
        }
        TStringConfig.getInstance().rootDir = rootDir;
    }

    /**
     * This function initializes all necessary variables based on the
     * commandline.<br>
     * <br>
     * This is powered by the java library <a
     * href="http://jcmdline.sourceforge.net/">jcmdline</a>
     *
     * @param args The commandline arguments
     */
    public static boolean initialize(String[] args) {
        TStringConfig configObj = TStringConfig.getInstance();
        JCommander commander = new JCommander(configObj);
        commander.setProgramName(TStringGlobals.applicationName);

        try {
            commander.parse(args);
        } catch (ParameterException e) {
            System.out.println("Error: " + e.getLocalizedMessage());
            commander.usage();

            return false;
        }

        if (TStringConfig.needsHelp()) {
            commander.usage();

            return false;
        }

        TStringConfig.setBackupDir(new File(TStringConfig.getRootDir(),
                "backups").getAbsoluteFile());

        return true;
    }

    /**
     * Hold the value that suppresses the creation of backups.
     */
    public static boolean isNoBackup() {
        return TStringConfig.getInstance().noBackup;
    }

    public static void setNoBackup(boolean noBackup) {
        TStringConfig.getInstance().noBackup = noBackup;
    }

    public static boolean needsHelp() {
        return TStringConfig.getInstance().help;
    }

    /**
     * An <code>enum</code> describing the type in which the program will run.
     *
     * @author m!m
     */
    public static enum Mode {
        /**
         * Signalizes the 'create' mode where the tstrings.tbl is created from
         * scratch.
         */
        CREATE,
        /**
         * Signaalizes the 'update' mode where the tstrings.tbl is only updated
         * leaving all deprecated or unused entries.
         */
        UPDATE
    }
}
