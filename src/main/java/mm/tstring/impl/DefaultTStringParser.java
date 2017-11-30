package mm.tstring.impl;

import mm.tstring.IFile;
import mm.tstring.ITstringParser;
import mm.tstring.objects.FileTString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultTStringParser implements ITstringParser {
    /**
     * The regexp-Pattern to match a XSTR-statement (thanks to Spicious).
     * Updated version provided by Goober5000
     */
    public static final Pattern tstringPattern = Pattern.compile("XSTR\\s*\\(\\s*\"([^\"]*)\"\\s*,\\s*(-?\\d+)\\s*\\)");

    /**
     * The pattern for the tech-add-intel-xstr SEXP
     * Regular expression was provided by Goober5000
     */
    public static final Pattern techIntelPattern = Pattern.compile("tech-add-intel-xstr\\s*(?:;;.*;;)?\\s*\"([^\"]*)\"\\s*(?:;;.*;;)?\\s*(-?\\d+)");

    public static final Pattern modifyVariablePattern =
            Pattern.compile("modify-variable-xstr\\s*(?:;;.*;;)?\\s*\"(?:[^\"]*)\"\\s*(?:;;.*;;)?\\s*\"([^\"]*)\"\\s*(?:;;.*;;)?\\s*(-?\\d+)");

    private static final Logger logger = LoggerFactory.getLogger(DefaultTStringParser.class);

    private static void findTStrings(List<FileTString> outList, Pattern patter, String content) {
        Matcher contentMatcher = patter.matcher(content);

        while (contentMatcher.find()) {
            String value = contentMatcher.group(1);
            String indexStr = contentMatcher.group(2);

            int index = Integer.parseInt(indexStr);

            outList.add(new FileTString(value, index, contentMatcher.start(2),
                    contentMatcher.end(2) - contentMatcher.start(2)));
        }
    }

    @Override
    public Collection<FileTString> parseStrings(IFile file) {
        String contents;
        try {
            contents = file.getContent();
        } catch (IOException e) {
            logger.error("Error while reading stream contents!", e);
            return Collections.emptyList();
        }

        List<FileTString> tstrings = new ArrayList<FileTString>();

        findTStrings(tstrings, tstringPattern, contents);
        findTStrings(tstrings, techIntelPattern, contents);
        findTStrings(tstrings, modifyVariablePattern, contents);

        return tstrings;
    }
}
