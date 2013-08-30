package mm.tstring.ops;

import mm.tstring.config.TStringConfig;
import mm.tstring.config.TStringConfig.Mode;

import java.io.File;

/**
 * Class to update an existing tstrings.tbl
 *
 * @author m!m
 */
public class TStringsUpdater extends TStringOperation
{

    public TStringsUpdater(File root, File backup)
    {
        super(root, backup);
    }

    /**
     * Updates the table based on the given input files.<br>
     * <br>
     * <i>This isn't doing anything currently</i>
     *
     * @param inputFiles The files which will be parsed.
     */
    @Override
    public boolean makeTString(File[] inputFiles)
    {
        File tstrings = new File(TStringConfig.getRootDir().getAbsolutePath() + "/data/tables/tstrings.tbl");
        if (tstrings.exists())
        {

        }
        else
        {
            System.err.println("No TStrings table found. Going to 'create'.");
            TStringConfig.setMode(Mode.CREATE);
            new TStringsCreator(TStringConfig.getRootDir(), TStringConfig.getBackupDir()).makeTString(inputFiles);
        }

        return true;
    }

}
