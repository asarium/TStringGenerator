package mm.tstring.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import mm.tstring.objects.TString;

/**
 * Class holding all values that are used in the whole generator.
 * 
 * @author m!m
 */
public class TStringGlobals
{
    public final static String        applicationName    = "@APP_NAME@";

    /**
     * The version of the genrator
     */
    public final static String        applicationVersion = "@APP_VERSION@";

    /**
     * The program will search for the files in these direcories
     */
    public static final String[]      searchDirs         = { "/data/tables",
            "/data/missions"                            };

    /**
     * The regexp-Pattern to match a XSTR-statement (thanks to Spicious).
     * Currently unused
     */
    public static final Pattern       tstringPattern     =
                                                                 Pattern.compile("XSTR\\(\"([^\"]*)\", ?(-?\\d+)\\)");

    /**
     * The list of currently known TStrings
     */
    public static final List<TString> tstrings           =
                                                                 new ArrayList<TString>();
}
