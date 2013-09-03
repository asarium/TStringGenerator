package mm.tstring.impl;

import mm.tstring.IFile;
import mm.tstring.ITstringParser;
import mm.tstring.objects.FileTString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultTStringParser implements ITstringParser
{
    /**
     * The regexp-Pattern to match a XSTR-statement (thanks to Spicious).
     * Updated version provided by Goober5000
     */
    public static final Pattern tstringPattern = Pattern.compile("XSTR\\s*\\(\\s*\"([^\"]*)\"\\s*,\\s*(-?\\d+)\\s*\\)");

    private static final Logger logger = LoggerFactory.getLogger(DefaultTStringParser.class);

    @Override
    public Collection<FileTString> parseStrings(IFile file)
    {
        List<FileTString> tstrings = new ArrayList<FileTString>();

        try
        {
            String contents = file.getContent();

            Matcher contentMatcher = tstringPattern.matcher(contents);

            while (contentMatcher.find())
            {
                String value = contentMatcher.group(1);
                String indexStr = contentMatcher.group(2);

                int index = Integer.parseInt(indexStr);

                tstrings.add(new FileTString(value, index, contentMatcher.start(),
                        contentMatcher.end() - contentMatcher.start()));
            }
        }
        catch (IOException e)
        {
            logger.error("Error while reading stream contents!", e);
        }

        return tstrings;
    }
}
