package mm.tstring.util;

import mm.tstring.objects.TString;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Class holding all values that are used in the whole generator.
 *
 * @author m!m
 */
public class TStringGlobals
{
    public final static String applicationName = "tstrings";

    /**
     * The program will search for the files in these directories
     */
    public static final String[] searchDirs = {"/data/tables", "/data/missions"};

    public static final Pattern tstringPattern = Pattern.compile("XSTR\\s*\\(\\s*\"([^\"]*)\"\\s*,\\s*(-?\\d+)\\s*\\)");

    /**
     * The list of currently known TStrings
     */
    public static final List<TString> tstrings = new ArrayList<TString>();
}
