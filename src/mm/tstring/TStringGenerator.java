package mm.tstring;

import java.io.File;

import mm.tstring.config.TStringConfig;
import mm.tstring.config.TStringConfig.Mode;
import mm.tstring.ops.TStringsCreator;
import mm.tstring.ops.TStringsUpdater;
import mm.tstring.util.Util;

/**
 * @author m!m
 * 
 */
public class TStringGenerator
{

    public static void main(String[] args)
    {
        TStringConfig.initialize(args);

        File[] parseArray = null;

        if (TStringConfig.getRootDir().exists())
        {
            parseArray = Util.getParseFiles();
        }

        if (parseArray == null || parseArray.length < 1)
        {
            System.out
                    .println("No files found that have to be parsed.\nNothing to be done...");
        }
        else if (TStringConfig.getMode() == Mode.CREATE)
        {
            new TStringsCreator(TStringConfig.getRootDir(),
                    TStringConfig.getBackupDir()).makeTString(parseArray);
        }
        else if (TStringConfig.getMode() == Mode.UPDATE)
        {
            new TStringsUpdater(TStringConfig.getRootDir(),
                    TStringConfig.getBackupDir()).makeTString(parseArray);
        }
    }
}
