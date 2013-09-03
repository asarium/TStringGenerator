package mm.tstring.impl;

import mm.tstring.MemoryFile;
import mm.tstring.objects.FileTString;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DefaultTStringParserTest
{
    @Test
    public void testParseStrings() throws Exception
    {
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

        assertFileTstringEquals(tstringList.get(0), "Test", 128, 0, 17);
        assertFileTstringEquals(tstringList.get(1), "Test1", 15, 18, 28);
        assertFileTstringEquals(tstringList.get(2), "Test 6", 9, 112, 17);
    }

    private void assertFileTstringEquals(FileTString tstring, String message, int index, long offset, long length)
    {
        Assert.assertNotNull(tstring);

        Assert.assertEquals(message, tstring.getValue());
        Assert.assertEquals(index, tstring.getIndex());
        Assert.assertEquals(offset, tstring.getOffset());
        Assert.assertEquals(length, tstring.getLength());
    }
}
