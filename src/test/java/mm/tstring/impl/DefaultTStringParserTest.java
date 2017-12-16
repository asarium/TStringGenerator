package mm.tstring.impl;

import mm.tstring.MemoryFile;
import mm.tstring.objects.FileTString;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DefaultTStringParserTest {
    @Test
    public void testParseStrings() throws Exception {
        DefaultTStringParser parser = new DefaultTStringParser();

        Collection<FileTString> tstrings = parser.parseStrings(new MemoryFile("testFile", "XSTR(\"Test\", 128)\n" +
                "XSTR    (    \"Test1\",    15)\n" +
                "XSTR \"Test2\", 8)\n" +
                "XSTR(\"Test3, )\n" +
                "XSTR(\"Test4\" 8)\n" +
                "XSTR(\"Test 5, 8)\n" +
                "XSTR(\"Test 6\", 9)"));

        Assert.assertEquals(3, tstrings.size());

        List<FileTString> tstringList = new ArrayList<FileTString>(tstrings);

        assertFileTstringEquals(tstringList.get(0), "Test", 128, 13, 3);
        assertFileTstringEquals(tstringList.get(1), "Test1", 15, 43, 2);
        assertFileTstringEquals(tstringList.get(2), "Test 6", 9, 127, 1);
    }

    @Test
    public void testTechIntel() throws Exception {
        String content = "$Formula: ( when \n" +
                "   ( true ) \n" +
                "   ( allow-weapon \"Flail\" ) \n" +
                "   ( allow-weapon \"Interceptor\" ) \n" +
                "   ( tech-add-weapons \n" +
                "      \"Flail\" \n" +
                "      \"Interceptor\" \n" +
                "   )\n" +
                ";;FSO 3.7.1;;   ( tech-add-intel-xstr \"Shivans\" 3197 )\n" +
                ")\n" +
                "+Name: New Tech!\n" +
                "+Repeat Count: 1\n" +
                "+Interval: 1\n" +
                "\n" +
                ";;FSO 3.7.1;; !*\n" +
                ";;FSO 3.5.1;; $Formula: ( when \n" +
                ";;FSO 3.5.1;;    ( true ) \n" +
                ";;FSO 3.5.1;;    ( tech-add-intel \"Shivans\" )\n" +
                ";;FSO 3.5.1;; ) \n" +
                ";;FSO 3.5.1;; +Name: Intel [3.5.1, 3.7.1)\n" +
                ";;FSO 3.5.1;; +Repeat Count: 1\n" +
                ";;FSO 3.5.1;; +Interval: 1\n" +
                ";;FSO 3.7.1;; *!\n" +
                "\n" +
                ";;FSO 3.7.1;; !*\n" +
                ";;FSO 3.5.1;; $Formula: ( when \n" +
                ";;FSO 3.5.1;;    ( true ) \n" +
                ";;FSO 3.5.1;;    ( tech-add-intel-xstr \n" +
                ";;FSO 3.5.1;;       \"Wereshivans\" \n" +
                ";;FSO 3.5.1;;       4208 \n" +
                ";;FSO 3.5.1;;    )\n" +
                ";;FSO 3.5.1;; ) \n" +
                ";;FSO 3.5.1;; +Name: Intel [3.5.1, 3.7.1)\n" +
                ";;FSO 3.5.1;; +Repeat Count: 1\n" +
                ";;FSO 3.5.1;; +Interval: 1\n" +
                ";;FSO 3.7.1;; *!";
        MemoryFile file = new MemoryFile("testFile", content);

        DefaultTStringParser parser = new DefaultTStringParser();

        Collection<FileTString> tstrings = parser.parseStrings(file);

        Assert.assertEquals(2, tstrings.size());

        List<FileTString> tstringList = new ArrayList<FileTString>(tstrings);

        assertFileTstringEquals(tstringList.get(0), "Shivans", 3197, 207, 4);
        assertFileTstringEquals(tstringList.get(1), "Wereshivans", 4208, 692, 4);
    }

    @Test
    public void testVariableXstr() throws Exception {
        String content = "#Events\t\t;! 1 total\n" +
                "\n" +
                "$Formula: ( when \n" +
                "   ( key-pressed \"1\" ) \n" +
                ";;FSO 3.7.1;;   ( modify-variable-xstr \n" +
                ";;FSO 3.7.1;;      \"@TranslateTest[Default]\" \n" +
                "      \"Untranslated\" \n" +
                "      3423\n" +
                ";;FSO 3.7.1;;   )\n" +
                ";;FSO 3.7.1;;)\n" +
                "+Name: Event name\n" +
                "+Repeat Count: 1\n" +
                "+Interval: 1" +
                "\n";
        MemoryFile file = new MemoryFile("testFile", content);

        DefaultTStringParser parser = new DefaultTStringParser();

        Collection<FileTString> tstrings = parser.parseStrings(file);

        Assert.assertEquals(1, tstrings.size());

        List<FileTString> tstringList = new ArrayList<FileTString>(tstrings);

        assertFileTstringEquals(tstringList.get(0), "Untranslated", 3423, 177, 4);
    }

    private void assertFileTstringEquals(FileTString tstring, String message, int index, long offset, long length) {
        Assert.assertNotNull(tstring);

        Assert.assertEquals(message, tstring.getValue());
        Assert.assertEquals(index, tstring.getIndex());
        Assert.assertEquals(offset, tstring.getIndexOffset());
        Assert.assertEquals(length, tstring.getIndexLength());
    }
}
