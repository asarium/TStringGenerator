package mm.tstring;

import mm.tstring.config.TStringConfig;
import mm.tstring.impl.DefaultTStringManager;
import mm.tstring.impl.DefaultTStringParser;
import mm.tstring.impl.ModDirectoryFileProvider;

/**
 * @author m!m
 */
public class TStringGenerator
{

    public static void main(String[] args)
    {
        if (!TStringConfig.initialize(args))
        {
            return;
        }

        IFileProvider fileProvider = new ModDirectoryFileProvider(TStringConfig.getRootDir(),
                TStringConfig.getBackupDir());
        ITstringParser parser = new DefaultTStringParser();

        ITStringManager manager = new DefaultTStringManager(TStringConfig.getMode());

        manager.collectTStrings(parser, fileProvider);
        manager.writeStrings(parser, fileProvider);
    }
}
