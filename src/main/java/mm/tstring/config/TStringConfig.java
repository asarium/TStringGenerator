package mm.tstring.config;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.converters.FileConverter;
import mm.tstring.util.TStringGlobals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @author m!m
 */
public class TStringConfig
{
    /**
     * An <code>enum</code> describing the type in which the program will run.
     *
     * @author m!m
     */
    public static enum Mode
    {
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

    private static final Logger logger = LoggerFactory.getLogger(TStringConfig.class);

    private static TStringConfig INSTANCE = TStringConfig.getInstance();

    /**
     * Returns the dir in which the backup will be stored
     *
     * @return The backup directory
     */
    public static File getBackupDir()
    {
        return TStringConfig.getInstance().backupDir;
    }

    private static TStringConfig getInstance()
    {
        if (TStringConfig.INSTANCE == null)
        {
            return new TStringConfig();
        }
        else
        {
            return TStringConfig.INSTANCE;
        }
    }

    /**
     * Sets a new backupdirectory
     *
     * @param backupDir The new directory. If it doesn't exist the directory will be
     *                  created.
     */
    public static void setBackupDir(File backupDir)
    {
        if (backupDir == null)
        {
            throw new NullPointerException();
        }

        if (!backupDir.exists())
        {
            if (!backupDir.mkdirs())
            {
                TStringConfig.logger.error("Couldn't create directory '" + backupDir.getAbsolutePath() + "'!");
            }
        }
        TStringConfig.getInstance().backupDir = backupDir;
    }

    /**
     * Gives the mode in which the generator will run.
     *
     * @return The Mode
     */
    public static Mode getMode()
    {
        return TStringConfig.getInstance().mode;
    }

    /**
     * Sets the new {@link Mode}. Only effectiv when used before or in
     * {@link #initialize(String[])}
     *
     * @param mode The new Mode
     * @see Mode
     */
    public static void setMode(Mode mode)
    {
        TStringConfig.getInstance().mode = mode;
    }

    /**
     * This function initializes all necessary variables based on the
     * commandline.
     *
     * @param args The commandline arguments
     */
    public static boolean initialize(String[] args)
    {
        TStringConfig configObj = TStringConfig.getInstance();
        JCommander commander = new JCommander(configObj);
        commander.setProgramName(TStringGlobals.applicationName);

        try
        {
            commander.parse(args);
        }
        catch (ParameterException e)
        {
            System.out.println("Error: " + e.getLocalizedMessage());
            commander.usage();

            return false;
        }

        if (TStringConfig.needsHelp())
        {
            commander.usage();

            return false;
        }

        TStringConfig.setBackupDir(new File(TStringConfig.getRootDir(), "backups").getAbsoluteFile());

        return true;
    }

    public static boolean needsHelp()
    {
        return TStringConfig.getInstance().help;
    }

    /**
     * This is the root Directory from where the missions and tables will be
     * searched
     *
     * @return The root directory
     */
    public static File getRootDir()
    {
        return TStringConfig.getInstance().rootDir;
    }

    /**
     * Sets the new root directory. If it doesn't exist it will be created.
     *
     * @param rootDir The new directory.
     */
    public static void setRootDir(File rootDir)
    {
        if (rootDir == null)
        {
            throw new NullPointerException();
        }

        if (!rootDir.exists())
        {
            if (!rootDir.mkdirs())
            {
                TStringConfig.logger.error("Couldn't create directory '" + rootDir.getAbsolutePath() + "'!");
                return;
            }
        }
        TStringConfig.getInstance().rootDir = rootDir;
    }

    private File backupDir = new File("backup").getAbsoluteFile();

    @Parameter(names = {"-h", "-help", "-?"},
            description = "Prints this help message")
    private boolean help = false;

    @Parameter(
            names = {"-m", "-mode"},
            converter = ModeConverter.class,
            description = "The mode in which this program will run. Defaults to 'create'.\n" + "'create' to create " +
                    "the tstring.tbl\n" + "'update' to update the tstring.tbl")
    private Mode mode = Mode.CREATE;

    @Parameter(
            names = {"-r", "-root"},
            converter = FileConverter.class,
            description = "The directory from which this programm will search in data/mission, data/scripts and data/tables")
    private File rootDir = new File(".").getAbsoluteFile();
}
